package com.example.weather.alert.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.weather.Constants
import com.example.weather.alert.AlertReceiver
import com.example.weather.alert.viewmodel.AlertViewModel
import com.example.weather.databinding.FragmentPopUpDialogBinding
import com.example.weather.model.AlertWeather
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class PopUpDialogFragment(val viewModel: AlertViewModel) : DialogFragment() {

    lateinit var binding : FragmentPopUpDialogBinding
    private  var startDate :Calendar = Calendar.getInstance()
    private  var endDate: Calendar = Calendar.getInstance()
    private  var startTime :Calendar = Calendar.getInstance()
    private  var endTime: Calendar = Calendar.getInstance()
    private var type : String = Constants.NOTIFICATION
    private lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopUpDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = requireActivity().getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)
        getCurrentDateAndTime()

        binding.fromDate.setOnClickListener {
            pickDate(binding.fromDate)
        }
        binding.fromTime.setOnClickListener {
            pickTime(binding.fromTime)
        }
        binding.toDate.setOnClickListener {
            pickDate(binding.toDate)
        }
        binding.toTime.setOnClickListener {
            pickTime(binding.toTime)
        }
        binding.oklBtn.setOnClickListener {
            val alert = AlertWeather(
                fromDate = binding.fromDate.text.toString(),
                toDate = binding.toDate.text.toString(),
                fromTime = binding.fromTime.text.toString(),
                toTime = binding.toTime.text.toString(),
                alertType = type,
                lat = sharedPreference.getFloat(Constants.LATITUDE,0.0F).toDouble(),
                lon = sharedPreference.getFloat(Constants.LONGITUDE,0.0F).toDouble())
            viewModel.insertAlert(alert)
            createAlertReceiver(alert)
            dialog?.dismiss()
        }
        binding.cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
        binding.typeRG.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->
            if (checkedId == binding.alarmRB.id){
                type = Constants.ALARM
            }else if (checkedId == binding.notificationRB.id){
                type = Constants.NOTIFICATION
            }
        })
    }


    private fun getCurrentDateAndTime(){
        val currentCalendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(currentCalendar.time)

        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val currentTime = timeFormat.format(currentCalendar.time)
        binding.fromDate.setText(currentDate)
        binding.fromTime.setText(currentTime)
        binding.toDate.setText(currentDate)
        binding.toTime.setText(currentTime)
    }

    private fun pickDate(text : TextInputEditText) {
        val currentDate: Calendar = Calendar.getInstance()
        val calender: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            AlertDialog.THEME_DEVICE_DEFAULT_DARK,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calender.set(year, month, dayOfMonth)
                text.setText(SimpleDateFormat("dd-MM-yyyy").format(calender.time))

                if (text == binding.fromDate){
                    startDate.set(year, month, dayOfMonth)
                }else if (text == binding.toDate){
                    endDate.set(year, month, dayOfMonth)
                }
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DATE)
        )
        datePickerDialog.datePicker.minDate = currentDate.getTimeInMillis()
        datePickerDialog.show()
    }

    private fun pickTime(text : TextInputEditText){
        val currentDate: Calendar= Calendar.getInstance()
        val calender: Calendar = Calendar.getInstance()
        val timePickerDialog =  TimePickerDialog(requireContext(), { view, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            text.setText(SimpleDateFormat("hh:mm").format(calender.time))

            if (text == binding.fromTime){
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                startTime.set(Calendar.MINUTE, minute)
            }else if (text == binding.toTime){
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                endTime.set(Calendar.MINUTE, minute)
            }
            },
            currentDate.get(Calendar.HOUR_OF_DAY),
            currentDate.get(Calendar.MINUTE)
            ,false);

        timePickerDialog.show();
    }

    private fun createAlertReceiver(alertWeather : AlertWeather){
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        intent.putExtra("alert", alertWeather)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startCalendar = Calendar.getInstance()
        startCalendar.set(startDate.get(Calendar.YEAR),
            startDate.get(Calendar.MONTH),
            startDate.get(Calendar.DATE),
            startTime.get(Calendar.HOUR_OF_DAY),
            startTime.get(Calendar.MINUTE),
            0)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DATE),
            endTime.get(Calendar.HOUR_OF_DAY),
            endTime.get(Calendar.MINUTE),
            0)

        val differenceInMillis = abs(startCalendar.timeInMillis - endCalendar.timeInMillis)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            differenceInMillis.toInt()+10,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        Log.i("time", "createAlertReceiver: $differenceInMillis")
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            startCalendar.timeInMillis +(differenceInMillis/2),
            pendingIntent
        )
    }
}


