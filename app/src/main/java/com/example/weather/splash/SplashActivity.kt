package com.example.weather.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.Constants
import com.example.weather.MainActivity
import com.example.weather.R
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SharedPreferences
    var language : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreference = getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)
        language = sharedPreference.getString(Constants.LANGUAGE, Constants.ENGLISH)

        if(language == Constants.ENGLISH){
            setLocal(Constants.ENGLISH)
        }else if (language == Constants.ARABIC){
            setLocal(Constants.ARABIC)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Handler().postDelayed(Runnable {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        }, 3500)
    }

    override fun onPause() {
        super.onPause()
        finish()
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
        this.recreate()
    }
}