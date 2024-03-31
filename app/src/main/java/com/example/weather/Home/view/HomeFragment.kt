package com.example.weather.Home.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Constants
import com.example.weather.Constants.REQUEST_CODE
import com.example.weather.Home.viewmodel.HomeViewModel
import com.example.weather.Home.viewmodel.HomeViewModelFactory
import com.example.weather.MainActivity
import com.example.weather.checkNetwork
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.getCurrentTime
import com.example.weather.model.APIState
import com.example.weather.model.CurrentDBState
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.model.WeatherResponse
import com.example.weather.network.WeatherRemoteDataSourceImp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel
    private lateinit var homeFactory : HomeViewModelFactory
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var sharedPreference: SharedPreferences
    var language : String? = ""
    private var unit : String? = ""
    private var speed : String? = ""
    var latitude : Double = 0.0
    var longitude : Double = 0.0
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
        sharedPreference = requireActivity().getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)
        language = sharedPreference.getString(Constants.LANGUAGE,Constants.ENGLISH)
        unit = sharedPreference.getString(Constants.TEMPERATURE,Constants.CELSIUS)
        speed = sharedPreference.getString(Constants.WIND_SPEED,Constants.METER_SECOND)
        latitude = sharedPreference.getFloat(Constants.LATITUDE,0.0F).toDouble()
        longitude = sharedPreference.getFloat(Constants.LONGITUDE,0.0F).toDouble()

        checkUnits()

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

        if (longitude != 0.0 && latitude != 0.0){
            if (checkNetwork(requireContext())){
                getWeatherFromApi(latitude,longitude)
            }else{
                getWeatherFromDB()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (longitude == 0.0 && latitude == 0.0){
            if(checkPermission()){
                if(isLocationEnabled()){
                    getFreshLocation()
                }else{
                    enableLocationServices()
                }
            }else{
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_CODE
                )
            }
        }
    }

    private fun drawScreen(weather : WeatherResponse){
        binding.cityTV.text = weather.city.name
        binding.dateTV.text = getCurrentTime(weather.list[0].dt, language!!)
        binding.temperatureTV.text = weather.list[0].main.temp.toInt().toString()+Constants.UNIT
        binding.descriptionTV.text  = weather.list[0].weather[0].description
        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.list[0].weather[0].icon+"@4x.png")
            .into(binding.weatherImage)
        binding.pressureTv.text = weather.list[0].main.pressure.toString()+" Pa"
        binding.humidityTv.text = weather.list[0].main.humidity.toString()+" %"
        binding.windTv.text = weather.list[0].wind.speed.toString()+Constants.speed
        binding.cloudTv.text = weather.list[0].clouds.all.toString()+" %"
        binding.ultravioletTv.text =weather.list[0].main.temp_max.toInt().toString()+Constants.UNIT+ " / "+ weather.list[0].main.temp_min.toInt().toString()+Constants.UNIT
        binding.visibilityTv.text = weather.list[0].visibility.toString()+" %"
    }

    private fun checkPermission(): Boolean {
        return checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun getFreshLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.Builder(0).apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build(),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        val location = locationResult.lastLocation
                        if (checkNetwork(requireContext())){
                            getWeatherFromApi(location!!.latitude, location.longitude)
                            sharedPreference.edit().putFloat(Constants.LATITUDE, location.latitude.toFloat()).apply()
                            sharedPreference.edit().putFloat(Constants.LONGITUDE, location.longitude.toFloat()).apply()
                        }else{
                            getWeatherFromDB()
                        }
                        /*viewModel.weather.observe(viewLifecycleOwner) { value ->
                        drawScreen(value)
                        hourAdapter.submitList(value.list.subList(0,8))
                        dayAdapter.submitList(value.list.chunked(8))
                    }*/
                        fusedLocationProviderClient.removeLocationUpdates(this);
                    }
                },
                Looper.myLooper()
            )
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE)
        }
    }

    private fun enableLocationServices() {
        Toast.makeText(requireContext(),"Turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("requestCode", "onRequestPermissionsResult: hello")
        if(requestCode == REQUEST_CODE){
            if (grantResults.size>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("requestCode", "onRequestPermissionsResult: hi")
                (requireActivity() as MainActivity).restart()
                getFreshLocation()
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE && grantResults.size > 0
            && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getFreshLocation()
        } else {
            Toast
                .makeText(activity, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    fun getWeatherFromApi(latitude : Double, longitude : Double ){
        viewModel.getWeather(
            latitude,
            longitude,
            unit,
            language
        )
        viewModel.deleteCurrentWeather()
        lifecycleScope.launch {
            viewModel.weatherApi.collectLatest { result ->
                when (result) {
                    is APIState.Loading -> {
                        Log.i("loading", "Loading: ")
                        binding.animationView.visibility = View.VISIBLE
                        binding.hourCV.visibility = View.GONE
                        binding.dailyCV.visibility = View.GONE
                        binding.detailsCV.visibility = View.GONE
                    }

                    is APIState.Success -> {
                        binding.animationView.visibility = View.GONE
                        binding.hourCV.visibility = View.VISIBLE
                        binding.dailyCV.visibility = View.VISIBLE
                        binding.detailsCV.visibility = View.VISIBLE
                        viewModel.insertCurrentWeather(result.data)
                        drawScreen(result.data)
                        hourAdapter.submitList(result.data.list.subList(0, 8))
                        dayAdapter.submitList(result.data.list.chunked(8))
                    }

                    else -> {

                    }
                }

            }
        }
    }
    fun getWeatherFromDB(){
        viewModel.getCurrentWeather()
        lifecycleScope.launch {
            viewModel.weatherDB.collectLatest {result ->
                when(result){
                    is CurrentDBState.Loading ->{
                        Log.i("loading", "Loading: ")
                        binding.animationView.visibility = View.VISIBLE
                        binding.hourCV.visibility = View.GONE
                        binding.dailyCV.visibility = View.GONE
                        binding.detailsCV.visibility = View.GONE
                    }
                    is CurrentDBState.Success ->{
                        delay(1500)
                        binding.animationView.visibility = View.GONE
                        binding.hourCV.visibility = View.VISIBLE
                        binding.dailyCV.visibility = View.VISIBLE
                        binding.detailsCV.visibility = View.VISIBLE
                        drawScreen(result.data[0])
                        hourAdapter.submitList(result.data[0].list.subList(0, 8))
                        dayAdapter.submitList(result.data[0].list.chunked(8))
                    }
                    else ->{

                    }
                }

            }
        }
    }

    private fun checkUnits(){
        if(unit == Constants.CELSIUS){
            Constants.UNIT = " °C"
        }else if(unit == Constants.FAHRENHEIT){
            Constants.UNIT = " °F"
        }else if(unit == Constants.KELVIN){
            Constants.UNIT = " °K"
        }

        if(speed == Constants.METER_SECOND){
            Constants.speed = " m/s"
        }else if(speed == Constants.MILE_HOUR){
            Constants.speed = " mi/h"
        }

    }

}