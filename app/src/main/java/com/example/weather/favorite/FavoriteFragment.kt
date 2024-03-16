package com.example.weather.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.weather.Home.view.HomeFragment
import com.example.weather.R
import com.example.weather.checkNetwork
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.map.MapFragment
import com.google.android.material.snackbar.Snackbar


class FavoriteFragment : Fragment() {
    lateinit var binding : FragmentFavoriteBinding
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
        binding.favFab.setOnClickListener {
            if (checkNetwork(requireContext())) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MapFragment()).commit()
            } else {
                Toast.makeText(requireContext(),"Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


}