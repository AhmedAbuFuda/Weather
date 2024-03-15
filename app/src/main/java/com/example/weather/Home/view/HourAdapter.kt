package com.example.weather.Home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.databinding.HourItemBinding
import com.example.weather.getHourTime
import com.example.weather.model.WeatherEntry
import com.example.weather.model.WeatherResponse

class HourAdapter (private var context: Context): ListAdapter<WeatherEntry, HourViewHolder>(WeatherDiffUtil()){
    private lateinit var binding : HourItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourItemBinding.inflate(inflater,parent,false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.temperatureHourTv.text = currentObj.main.temp.toInt().toString()+" °C"
        holder.binding.timeHourTv.text = getHourTime(currentObj.dt)
        Glide.with(context).load("https://openweathermap.org/img/wn/"+ currentObj.weather[0].icon+"@2x.png")
            .into(holder.binding.iconWeatherIv)
    }
}
class HourViewHolder (var binding: HourItemBinding): RecyclerView.ViewHolder(binding.root)