package com.example.wetherforecastapp.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel (application: Application) : AndroidViewModel(application) {

    val apiObj = MutableLiveData<WeatherData>();
    var apiRepository: WeatherRepository
    var localDataSource: LocalDataSource

    init {
        apiRepository = WeatherRepository(application)
        localDataSource = LocalDataSource(application)
    }

    public fun loadWeather(context: Context, lat: Double, lon: Double, lang: String, unit: String) {
         apiRepository.fetchWeatherObj(context, lat, lon, lang, unit)
    }
}