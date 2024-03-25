package com.example.weather.map.view

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentMapBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.favorite.view.FavoriteFragment
import com.example.weather.map.viewmodel.MapViewModel
import com.example.weather.map.viewmodel.MapViewModelFactory
import com.example.weather.model.APIState
import com.example.weather.model.FavoritePlace
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.network.WeatherRemoteDataSourceImp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


class MapFragment : Fragment() , OnMapReadyCallback {
    lateinit var map : GoogleMap
    private var marker: Marker? = null
    lateinit var binding: FragmentMapBinding
    private lateinit var viewModel : MapViewModel
    private lateinit var mapFactory : MapViewModelFactory
    var  address : Address? = null
    lateinit var latLng : LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFactory=MapViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(), WeatherLocalDataSourceImp(requireContext())
            ))
        viewModel = ViewModelProvider(this,mapFactory)[MapViewModel::class.java]

        binding.mapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location: String = binding.mapSearch.query.toString()
                var addressList: List<Address>? = null
                if (!location.isNullOrEmpty()) {
                    val geocoder = Geocoder(requireContext())
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                     address = addressList?.firstOrNull()
                    if (address != null) {
                        latLng = LatLng(address!!.latitude, address!!.longitude)
                        map.addMarker(
                            MarkerOptions().position(latLng).title(location)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                        binding.save.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        mapFragment.getMapAsync(this)

        binding.save.setOnClickListener {
            if (marker != null){
                val favoritePlace = FavoritePlace(latitude = marker!!.position.latitude, longitude = marker!!.position.longitude, address = getAddressGeoCoder(marker!!.position.latitude,marker!!.position.longitude))
                viewModel.insertFavoritePlace(favoritePlace)
            }else{
                val favoritePlace = FavoritePlace(latitude = address!!.latitude, longitude = address!!.longitude, address = getAddressGeoCoder(address!!.latitude,address!!.longitude))
                viewModel.insertFavoritePlace(favoritePlace)
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavoriteFragment()).commit()

            Log.i("TAG", "onViewCreated: "+ (marker?.position?.latitude))
            Log.i("TAG", "onViewCreated: "+ (marker?.position?.longitude))
            Log.i("TAG", "onViewCreated: "+ (marker?.title))
        }

    }

    override fun onMapReady(googleMap : GoogleMap) {
        map = googleMap
        setMapLongClick(map)
        setPoiClick(map)
    }

    private fun setMapLongClick(map: GoogleMap) {

        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            map.clear()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            binding.save.visibility = View.VISIBLE
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker?.showInfoWindow()
            binding.save.visibility = View.VISIBLE
        }
    }

    private fun getAddressGeoCoder(latitude: Double?, longitude: Double?): String {
        var address = ""
        val geocoder = Geocoder(requireContext(),  Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
        if (addresses != null && addresses.size > 0) {
            val city = addresses!![0].locality
            val country = addresses[0].countryName
            if(city == null){
                address = country
            }else{
                address = "$country / $city"
            }
            if(city == null && country == null)
                address = "Unknown Place"
        }
        return address
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


}