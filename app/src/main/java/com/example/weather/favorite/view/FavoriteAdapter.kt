package com.example.weather.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.Home.view.HourViewHolder
import com.example.weather.Home.view.WeatherDiffUtil
import com.example.weather.databinding.FavoriteItemBinding
import com.example.weather.databinding.WeakItemBinding
import com.example.weather.getDay
import com.example.weather.model.FavoritePlace
import com.example.weather.model.FavoriteWeather
import com.example.weather.model.WeatherEntry

class FavoriteAdapter (private var context: Context, var action : (FavoritePlace) -> Unit, var delete : (Int) -> Unit) :
    ListAdapter<FavoritePlace, FavoriteViewHolder>(FavoriteDiffUtil()){
    private lateinit var binding : FavoriteItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteItemBinding.inflate(inflater,parent,false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.placeTV.text = currentObj.address
        holder.binding.deleteBtn.setOnClickListener { delete(currentObj.id) }
        holder.binding.favoriteCardView.setOnClickListener { action(currentObj) }
    }
}
class FavoriteViewHolder (var binding: FavoriteItemBinding): RecyclerView.ViewHolder(binding.root)
