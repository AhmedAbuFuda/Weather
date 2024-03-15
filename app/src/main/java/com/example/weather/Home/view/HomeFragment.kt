package com.example.weather.Home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Constants.REQUEST_CODE
import com.example.weather.Home.viewmodel.HomeViewModel
import com.example.weather.Home.viewmodel.HomeViewModelFactory
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.getCurrentTime
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.model.WeatherResponse
import com.example.weather.network.WeatherRemoteDataSourceImp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.log

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel
    private lateinit var homeFactory : HomeViewModelFactory
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hourLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.HORIZONTAL, false)
        hourAdapter = HourAdapter(requireContext())
        binding.hourlyRV.apply {
            adapter = hourAdapter
            layoutManager = hourLayoutManager
        }

        dayLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
        dayAdapter = DayAdapter(requireContext())
        binding.weakRV.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        homeFactory=HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(), WeatherLocalDataSourceImp(requireContext())
            ))
        viewModel = ViewModelProvider(this,homeFactory)[HomeViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        if(checkPermission()){
            if(isLocationEnabled()){
                getFreshLocation()
            }else{
                enableLocationServices()
            }
        }else{
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE)
        }
    }

    private fun drawScreen(weather : WeatherResponse){
        binding.cityTV.text = weather.city.name
        binding.dateTV.text = getCurrentTime(weather.list[0].dt)
        binding.temperatureTV.text = weather.list[0].main.temp.toInt().toString()+" °C"
        binding.descriptionTV.text  = weather.list[0].weather[0].description
        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.list[0].weather[0].icon+"@4x.png")
            .into(binding.weatherImage)
        binding.pressureTv.text = weather.list[0].main.pressure.toString()
        binding.humidityTv.text = weather.list[0].main.humidity.toString()
        binding.windTv.text = weather.list[0].wind.speed.toString()
        binding.cloudTv.text = weather.list[0].clouds.all.toString()
        binding.ultravioletTv.text =weather.list[0].main.temp_max.toInt().toString()+ " / "+ weather.list[0].main.temp_min.toInt().toString()
        binding.visibilityTv.text = weather.list[0].visibility.toString()
    }

    private fun checkPermission(): Boolean {
        return checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    viewModel.getWeather(location?.latitude!!,location?.longitude!!,"metric","en")
                    viewModel.weather.observe(viewLifecycleOwner) { value ->
                        drawScreen(value)
                        hourAdapter.submitList(value.list.subList(0,8))
                        dayAdapter.submitList(value.list.chunked(8))
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this);
                }
            },
            Looper.myLooper()
        )
    }

    private fun enableLocationServices() {
        Toast.makeText(requireContext(),"Turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            if (grantResults.size>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getFreshLocation()
            }
        }
    }

}