package com.example.weather.alert.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.alert.viewmodel.AlertViewModel
import com.example.weather.alert.viewmodel.AlertViewModelFactory
import com.example.weather.checkNetwork
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.db.WeatherLocalDataSourceImp
import com.example.weather.model.AlertDBState
import com.example.weather.model.AlertWeather
import com.example.weather.model.WeatherRepositoryImp
import com.example.weather.network.WeatherRemoteDataSourceImp
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlertFragment : Fragment(), AlertOnClickListener {
    private lateinit var binding: FragmentAlertBinding
    companion object{
        lateinit var alertViewModel: AlertViewModel
    }
    private lateinit var alertFactory: AlertViewModelFactory
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        callPermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        alertAdapter = AlertAdapter(requireContext(),
            action = { alertWeather -> onClickDelete(alertWeather) })
        binding.alertRV.apply {
            adapter = alertAdapter
            layoutManager = alertLayoutManager
        }

        alertFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                WeatherLocalDataSourceImp(requireContext())
            )
        )
        alertViewModel = ViewModelProvider(this, alertFactory)[AlertViewModel::class.java]
        getAlerts()

        binding.alertFab.setOnClickListener {
            if (checkNetwork(requireContext())) {
                val popUpDialog = PopUpDialogFragment(alertViewModel)
                popUpDialog.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")

            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onClickDelete(alertWeather: AlertWeather) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_alert))
            .setMessage(getString(R.string.message_alert))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                alertViewModel.deleteAlert(alertWeather)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getAlerts() {
        alertViewModel.getAlertsWeather()
        lifecycleScope.launch {
            alertViewModel.alertDB.collectLatest { result ->
                when (result) {
                    is AlertDBState.Loading -> {
                        binding.alertAnimation.visibility = View.VISIBLE
                        binding.alertRV.visibility = View.GONE
                        binding.alertFab.visibility = View.GONE
                    }

                    is AlertDBState.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            binding.alertAnimation.visibility = View.VISIBLE
                            binding.alertRV.visibility = View.GONE
                            binding.alertFab.visibility = View.VISIBLE
                        } else {
                            delay(1500)
                            binding.alertAnimation.visibility = View.GONE
                            binding.alertRV.visibility = View.VISIBLE
                            binding.alertFab.visibility = View.VISIBLE
                            alertAdapter.submitList(result.data)
                        }
                    }

                    else -> {
                        Log.i("Error", "Error: ")
                    }
                }

            }
        }
    }

    private fun callPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            requireActivity().setShowWhenLocked(true)
            requireActivity().setTurnScreenOn(true)
        }
        if (!Settings.canDrawOverlays(requireActivity())) {
            permissionsDialog()
        }
    }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireContext().packageName)
            )
            someActivityResultLauncher.launch(intent)
        }
    }

    private fun permissionsDialog() {
        android.app.AlertDialog.Builder(requireActivity())
            .setTitle("Permission Request")
            .setCancelable(false)
            .setMessage("Please allow Display other Apps Permission")
            .setPositiveButton(
                "Yes"
            ) { _, _ -> checkPermission() }.setNegativeButton(
                "No"
            ) { _, _ -> Toast.makeText(requireContext(),"Unfortunately Can not use the alarm without allow Permission",Toast.LENGTH_LONG).show()
            }.show()
    }

    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(requireContext())) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.alarm_not_show),
                    Toast.LENGTH_LONG
                ).setAction("Enable") {
                    enableIt()
                }.show()
            }
        }

    private fun enableIt() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireContext().packageName)
        )
        someActivityResultLauncher.launch(intent)
    }


}



