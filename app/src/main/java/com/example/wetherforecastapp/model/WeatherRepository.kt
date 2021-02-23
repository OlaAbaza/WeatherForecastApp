package com.example.wetherforecastapp.model

import android.app.Application
import androidx.lifecycle.liveData
import com.example.wetherforecastapp.model.local.LocalDataSource
import com.example.wetherforecastapp.model.remote.Resource
import com.example.wetherforecastapp.model.remote.WeatherService
import kotlinx.coroutines.Dispatchers

class WeatherRepository {
    lateinit var localDataSource: LocalDataSource
    lateinit var weatherService: WeatherService
   constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherService
    }

    fun getWeather() {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                val WeatherData=weatherService.apiService.getCurrentWeatherByLatLng(
                    33.441792,
                    -94.037689
                )
                emit(

                    Resource.success(
                        data = WeatherData
                    )

                )
                localDataSource.insert(WeatherData)

            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }
}