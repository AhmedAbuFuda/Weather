package com.example.weather.setting

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weather.Constants
import com.example.weather.Home.view.HomeFragment
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.checkNetwork
import com.example.weather.databinding.FragmentSettingBinding
import com.example.weather.splash.SplashActivity
import java.util.Locale


class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding
    private lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isChecked()
        if (!checkNetwork(requireContext())){
            binding.gpsRB.isEnabled = false
            binding.mapRB.isEnabled = false
            binding.meterRB.isEnabled = false
            binding.mileRB.isEnabled = false
            binding.celsiusRB.isEnabled = false
            binding.fahrenheitRB.isEnabled = false
            binding.kelvinRB.isEnabled = false

            Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
        }

        sharedPreference = requireActivity().getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)

        binding.locationRG.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.gpsRB.id) {
                sharedPreference.edit().putString(Constants.LOCATION, Constants.GPS).apply()
                sharedPreference.edit().putFloat(Constants.LONGITUDE,0.0F).apply()
                sharedPreference.edit().putFloat(Constants.LATITUDE,0.0F).apply()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, HomeFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }else if (checkedId == binding.mapRB.id){
                sharedPreference.edit().putString(Constants.LOCATION, Constants.MAP).apply()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, MapSetting())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        binding.languageRG.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            if (checkedId == binding.englishRB.id){
                sharedPreference.edit().putString(Constants.LANGUAGE, Constants.ENGLISH).apply()
                setLocal("en")
            }else if (checkedId == binding.arabicRB.id){
                sharedPreference.edit().putString(Constants.LANGUAGE, Constants.ARABIC).apply()
                setLocal("ar")
            }
        })
        binding.windSpeedRG.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{group, checkedId ->
            if (checkedId == binding.meterRB.id){
                sharedPreference.edit().putString(Constants.WIND_SPEED,Constants.METER_SECOND).apply()
            }else if (checkedId == binding.mileRB.id){
                sharedPreference.edit().putString(Constants.WIND_SPEED,Constants.MILE_HOUR).apply()
            }
        })
        binding.temperatureRG.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{group, checkedId ->
                if (checkedId == binding.celsiusRB.id){
                    sharedPreference.edit().putString(Constants.TEMPERATURE,Constants.CELSIUS).apply()
                }else if (checkedId == binding.fahrenheitRB.id){
                    sharedPreference.edit().putString(Constants.TEMPERATURE,Constants.FAHRENHEIT).apply()
                }else if (checkedId == binding.kelvinRB.id){
                    sharedPreference.edit().putString(Constants.TEMPERATURE,Constants.KELVIN).apply()
                }

        })

    }

    private fun isChecked() {

        val sharedPreference =
            requireActivity().getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)
        val location = sharedPreference.getString(Constants.LOCATION, "GPS")
        val language = sharedPreference.getString(Constants.LANGUAGE, "en")
        val temperature = sharedPreference.getString(Constants.TEMPERATURE, "metric")
        val windSpeed = sharedPreference.getString(Constants.WIND_SPEED, "meter/sec")

        if (location == Constants.GPS) {
            binding.locationRG.check(binding.gpsRB.id)
        }

        if (location == Constants.MAP) {
            binding.locationRG.check(binding.mapRB.id)
        }

        if (language == Constants.ENGLISH) {
            binding.languageRG.check(binding.englishRB.id)
        }
        if (language == Constants.ARABIC) {
            binding.languageRG.check(binding.arabicRB.id)
        }

        if (windSpeed == Constants.METER_SECOND) {
            binding.windSpeedRG.check(binding.meterRB.id)
        }
        if (windSpeed == Constants.MILE_HOUR) {
            binding.windSpeedRG.check(binding.mileRB.id)
        }

        if (temperature == Constants.CELSIUS) {
            binding.temperatureRG.check(binding.celsiusRB.id)
        }
        if (temperature == Constants.FAHRENHEIT) {
            binding.temperatureRG.check(binding.fahrenheitRB.id)
        }
        if (temperature == Constants.KELVIN) {
            binding.temperatureRG.check(binding.kelvinRB.id)
        }
    }

    private fun setLocal(language: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            config.setLocale(Locale(language))
            config.setLayoutDirection(Locale(language))
        }
        else
        {
            config.locale = Locale(language)
        }
        resources.updateConfiguration(config, dm)
        (requireActivity() as MainActivity).restart()
    }


}