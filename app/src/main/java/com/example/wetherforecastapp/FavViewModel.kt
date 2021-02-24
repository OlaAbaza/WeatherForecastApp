package com.example.wetherforecastapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData

class FavViewModel(application: Application) : AndroidViewModel(application) {

    val apiObj= MutableLiveData<WeatherData>();
    var apiRepository: WeatherRepository

    init{
        apiRepository = WeatherRepository(application)
    }

    public fun loadWeather(context: Context, lat:Double, lon:Double) : LiveData<List<WeatherData>> {
        return apiRepository.fetchWeatherData(context,lat, lon)
    }
    public fun getWeatherList() : LiveData<List<WeatherData>> {
        return apiRepository.getWeatherData()
    }
    public fun getApiObj() : LiveData<WeatherData> {
        return apiObj;
    }

}