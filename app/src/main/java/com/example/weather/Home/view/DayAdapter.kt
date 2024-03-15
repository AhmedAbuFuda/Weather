package com.example.weather.Home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.databinding.HourItemBinding
import com.example.weather.databinding.WeakItemBinding
import com.example.weather.getDay
import com.example.weather.getHourTime
import com.example.weather.model.WeatherEntry

class DayAdapter (private var context: Context): ListAdapter<List<WeatherEntry>, WeakViewHolder>(DayDiffUtil()){
    private lateinit var binding : WeakItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeakViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = WeakItemBinding.inflate(inflater,parent,false)
        return WeakViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeakViewHolder, position: Int) {
        val currentObj = getItem(position)[position]
        holder.binding.dayTV.text = getDay(currentObj.dt)
        holder.binding.temperatureDescriptionTv.text = currentObj.weather[0].description
        holder.binding.maxMinTemperatureTv.text = currentObj.main.temp_max.toInt().toString()+ "° / "+ currentObj.main.temp_min.toInt().toString()+"°"
        Glide.with(context).load("https://openweathermap.org/img/wn/"+ currentObj.weather[0].icon+"@2x.png")
            .into(holder.binding.weatherIcon)
    }
}
class WeakViewHolder (var binding: WeakItemBinding): RecyclerView.ViewHolder(binding.root)