package com.example.weather.favoritedetalis

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Constants
import com.example.weather.Home.view.DayAdapter
import com.example.weather.Home.view.HourAdapter
import com.example.weather.Home.viewmodel.HomeViewModel
import com.example.weather.Home.viewmodel.HomeViewModelFactory
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.getCurrentTime
import com.example.weather.model.APIState
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.model.WeatherResponse
import com.example.weather.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteDetails : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel
    private lateinit var homeFactory : HomeViewModelFactory
    private lateinit var hourAdapter: HourAdapter
    private lateinit var hourLayoutManager: LinearLayoutManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var sharedPreference: SharedPreferences
    var language : String? = ""
    var unit : String? = ""
    var latitude : Double? = 0.0
    var longitude : Double?= 0.0
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
        language = sharedPreference.getString(Constants.LANGUAGE, Constants.ENGLISH)
        unit = sharedPreference.getString(Constants.TEMPERATURE,Constants.CELSIUS)

        if(unit == Constants.CELSIUS){
            Constants.UNIT = " °C"
        }else if(unit == Constants.FAHRENHEIT){
            Constants.UNIT = " °F"
        }else if(unit == Constants.KELVIN){
            Constants.UNIT = " °K"
        }

        val bundle = arguments
        if (bundle != null) {
            val details = bundle.getParcelable<FavoritePlace>("data")
            longitude = details?.longitude
            latitude = details?.latitude

        }

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

        viewModel.getWeather(latitude!!,longitude!!,unit,language)
        lifecycleScope.launch {
            viewModel.weatherApi.collectLatest { result ->
                when(result){
                    is APIState.Loading ->{
                        Log.i("loading", "Loading: ")
                        binding.animationView.visibility = View.VISIBLE
                        binding.hourCV.visibility = View.GONE
                        binding.dailyCV.visibility = View.GONE
                        binding.detailsCV.visibility = View.GONE
                    }

                    is APIState.Success ->{
                        binding.animationView.visibility = View.GONE
                        binding.hourCV.visibility = View.VISIBLE
                        binding.dailyCV.visibility = View.VISIBLE
                        binding.detailsCV.visibility = View.VISIBLE
                        drawScreen(result.data)
                        hourAdapter.submitList(result.data.list.subList(0,8))
                        dayAdapter.submitList(result.data.list.chunked(8))
                    }

                    else ->{
                        Log.i("Error", "Error: ")
                    }
                }

            }
        }
    }


    private fun drawScreen(weather : WeatherResponse){
        binding.cityTV.text = weather.city.name
        binding.dateTV.text = getCurrentTime(weather.list[0].dt,language!!)
        binding.temperatureTV.text = weather.list[0].main.temp.toInt().toString()+Constants.UNIT
        binding.descriptionTV.text  = weather.list[0].weather[0].description
        Glide.with(requireContext()).load("https://openweathermap.org/img/wn/"+ weather.list[0].weather[0].icon+"@4x.png")
            .into(binding.weatherImage)
        binding.pressureTv.text = weather.list[0].main.pressure.toString()+" Pa"
        binding.humidityTv.text = weather.list[0].main.humidity.toString()+" %"
        binding.windTv.text = weather.list[0].wind.speed.toString()+" m/s"
        binding.cloudTv.text = weather.list[0].clouds.all.toString()+" %"
        binding.ultravioletTv.text =weather.list[0].main.temp_max.toInt().toString()+Constants.UNIT+ " / "+ weather.list[0].main.temp_min.toInt().toString()+Constants.UNIT
        binding.visibilityTv.text = weather.list[0].visibility.toString()+" %"
    }
}