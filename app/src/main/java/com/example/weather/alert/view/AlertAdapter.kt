package com.example.weather.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.AlertItemBinding
import com.example.weather.model.AlertWeather


class AlertAdapter(private var context: Context, var action : (AlertWeather) -> Unit) :
    ListAdapter<AlertWeather, AlertViewHolder>(AlertDiffUtil()){

    private lateinit var binding : AlertItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater,parent,false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.fromDateTime.text = currentObj.fromDate + " | " + currentObj.fromTime
        holder.binding.toDateTime.text = currentObj.toDate + " | " + currentObj.toTime
        holder.binding.deleteBtn.setOnClickListener { action(currentObj) }
    }


}

class AlertViewHolder (var binding: AlertItemBinding): RecyclerView.ViewHolder(binding.root)
