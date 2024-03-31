package com.example.weather.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.weather.Constants
import com.example.weather.R
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.model.AlertWeather
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.model.WeatherResponse
import com.example.weather.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertReceiver : BroadcastReceiver() {

    private lateinit var sharedPreference: SharedPreferences
    override fun onReceive(context: Context, intent: Intent?) {
        val alertWeather = intent?.getSerializableExtra("alert") as AlertWeather
        val repo = WeatherRepositoryImp.getInstance(
            WeatherRemoteDataSourceImp.getInstance(),
            WeatherLocalDataSourceImp(context)
        )
        getWeatherFromApi(context,alertWeather,repo)
        deleteAlert(repo,alertWeather)
    }

    private fun getWeatherFromApi(context: Context, alertWeather: AlertWeather, repo : WeatherRepositoryImp){
        sharedPreference = context.getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val weather = repo.getWeatherFromApi(alertWeather.lat,alertWeather.lon,
                sharedPreference.getString(Constants.TEMPERATURE,Constants.CELSIUS),
                sharedPreference.getString(Constants.LANGUAGE,Constants.ENGLISH))

            weather.collectLatest { result ->
                if(alertWeather.alertType == Constants.NOTIFICATION){
                    notification(context,result)
                }else{
                    alarm(context,result)
                }
            }
        }
    }

    private fun notification(context : Context, weather: WeatherResponse){
        val builder = NotificationCompat.Builder(context,Constants.WEATHER_NOTIFICATION)
            .setSmallIcon(R.mipmap.weather_icon)
            .setContentTitle(context.resources.getString(R.string.Weatheralert))
            .setContentText("${context.resources.getString(R.string.`in`)} ${weather.city.name}, ${context.resources.getString(R.string.weather)} ${weather.list[0].weather[0].description} ${context.resources.getString(R.string.temper)} ${weather.list[0].main.temp.toInt()} ${Constants.UNIT} ${context.resources.getString(R.string.now)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.WEATHER_NOTIFICATION,
                "Weather Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(Constants.WEATHER_NOTIFICATION)
            notificationManager.notify(0, builder.build())
        }
        Log.i("alert", "onReceive: ${weather.list[0].main.temp.toInt()}")
        Log.i("alert", "onReceive: ${weather.list[0].weather[0].description}")
    }

    private suspend fun alarm(context: Context, weather: WeatherResponse) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.alarm_dialog, null, false)
        val cancelBtn = view.findViewById<Button>(R.id.canacelBtn)
        val description = view.findViewById<TextView>(R.id.description)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            description.text = "${context.resources.getString(R.string.`in`)} ${weather.city.name}, ${context.resources.getString(R.string.weather)} ${weather.list[0].weather[0].description} ${context.resources.getString(R.string.temper)} ${weather.list[0].main.temp.toInt()} ${Constants.UNIT} ${context.resources.getString(R.string.now)}"
        }

        val mediaPlayer = MediaPlayer.create(context, R.raw.weather_alert)
        mediaPlayer.start()
        mediaPlayer.isLooping = true
        cancelBtn.setOnClickListener {
            mediaPlayer?.stop()
            windowManager.removeView(view)
        }
    }

    private fun deleteAlert(repo : WeatherRepositoryImp, alertWeather: AlertWeather ){
        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteAlert(alertWeather)
        }
    }
}