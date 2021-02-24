package com.example.wetherforecastapp.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.wetherforecastapp.model.entity.WeatherData

class LocalDataSource {
    var weatherDao: WeatherDao


    constructor(application: Application)  {
        weatherDao = WeatherDatabase.getDatabase(application).weatherDao()
    }

    fun getData(): LiveData<List<WeatherData>> {
        return weatherDao.getWeatherData()
    }

    suspend fun insert(weatherData: WeatherData) {
        weatherDao.insert(weatherData)
    }
}
