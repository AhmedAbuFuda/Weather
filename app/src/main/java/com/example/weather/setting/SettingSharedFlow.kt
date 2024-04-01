package com.example.weather.setting

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingSharedFlow(externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 5000,
    context: Context) {

    private val _sharedFlow = MutableSharedFlow<String>(replay = 3)
    var sharedFlow: SharedFlow<String> = _sharedFlow
    var sharedPreference: SharedPreferences = context.getSharedPreferences(Constants.SETTING_SHARED, Context.MODE_PRIVATE)

    val language = sharedPreference.getString(Constants.LANGUAGE, Constants.ENGLISH)
    init {
        externalScope.launch {
            while(true) {
                if (language != null) {
                    _sharedFlow.emit(language)
                }
                delay(tickIntervalMs)
            }
        }
    }


}