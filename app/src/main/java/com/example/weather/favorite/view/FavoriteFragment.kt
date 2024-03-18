package com.example.weather.favorite.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.checkNetwork
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.favorite.viewmodel.FavoriteViewModel
import com.example.weather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weather.favoritedetalis.FavoriteDetails
import com.example.weather.map.view.MapFragment
import com.example.weather.model.FavoritePlace
import com.example.weather.model.PlaceDBState
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), FavoriteClickListener {
    private lateinit var binding : FragmentFavoriteBinding
    private lateinit var viewModel : FavoriteViewModel
    private lateinit var favoriteFactory : FavoriteViewModelFactory
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        favoriteLayoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
        favoriteAdapter = FavoriteAdapter(requireContext(),
            action = { favoriteWeather -> onClick(favoriteWeather)},
            delete = { id -> onClickDelete(id)})
        binding.favRV.apply {
            adapter = favoriteAdapter
            layoutManager = favoriteLayoutManager
        }

        favoriteFactory= FavoriteViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(), WeatherLocalDataSourceImp(requireContext())
            ))
        viewModel = ViewModelProvider(this,favoriteFactory)[FavoriteViewModel::class.java]
        //getWeatherForFavoritePlace();
        viewModel.getFavoritePlace()
        lifecycleScope.launch {
            viewModel.favoriteDB.collectLatest {result ->
                when(result){
                    is PlaceDBState.Loading ->{
                        binding.favoriteAnimation.visibility= View.VISIBLE
                        binding.favRV.visibility = View.GONE
                        binding.favFab.visibility = View.GONE
                    }
                    is PlaceDBState.Success ->{
                        if (result.data.isNullOrEmpty()){
                            binding.favoriteAnimation.visibility= View.VISIBLE
                            binding.favRV.visibility = View.GONE
                            binding.favFab.visibility = View.VISIBLE
                        }else {
                            delay(1500)
                            binding.favoriteAnimation.visibility = View.GONE
                            binding.favRV.visibility = View.VISIBLE
                            binding.favFab.visibility = View.VISIBLE
                            favoriteAdapter.submitList(result.data)
                        }
                    }

                    else ->{
                        Log.i("Error", "Error: ")
                    }
                }
            }
        }

        binding.favFab.setOnClickListener {
            if (checkNetwork(requireContext())) {
                val fragment = MapFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                Toast.makeText(requireContext(),"No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /*private fun getWeatherForFavoritePlace(){
        viewModel.getFavoritePlace()
        lifecycleScope.launch {
            viewModel.favoriteDB.collectLatest {result ->
                when(result){
                    is PlaceDBState.Loading ->{
                        Log.i("loading", "Loading: ")
                    }

                    is PlaceDBState.Success ->{
                        getWeatherApi(result.data)
                    }

                    else ->{
                        Log.i("Error", "Error: ")
                    }
                }
            }
        }
    }*/

    /*private fun getWeatherApi(list: List<FavoritePlace>) {
        lifecycleScope.launch {
            for (fav in list) {
                val favoriteWeather = fetchWeatherData(fav.latitude, fav.longitude)
                viewModel.insertFavoriteWeather(favoriteWeather)
            }
        }
    }*/

    /*private suspend fun fetchWeatherData(latitude: Double, longitude: Double): FavoriteWeather {
        var favoriteWeather = FavoriteWeather()
        viewModel.getWeather(latitude, longitude, "metric", "en")
        viewModel.weatherApi.collectLatest { result ->
            when (result) {
                is APIState.Loading -> {
                    Log.i("loading", "Loading: ")
                }

                is APIState.Success -> {
                    Log.i("Done", "getWeatherApi: done")
                    favoriteWeather = FavoriteWeather(
                        cod = result.data.cod, message = result.data.message,
                        list = result.data.list, cnt = result.data.cnt, city = result.data.city
                    )
                }

                else -> {
                    Log.i("Error", "Error: ")
                }
            }
        }
        return favoriteWeather
    }*/

    override fun onClick(favoritePlace: FavoritePlace) {
        if(checkNetwork(requireContext())){
            val fragment = FavoriteDetails()
            val bundle = Bundle()
            bundle.putParcelable("data", favoritePlace)
            fragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }else{
            Toast.makeText(requireContext(),"No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickDelete(id : Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title))
            .setMessage(getString(R.string.message))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteFavoritePlace(id)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}