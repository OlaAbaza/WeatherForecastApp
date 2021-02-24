package com.example.wetherforecastapp.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData

class MainViewModel (application: Application) : AndroidViewModel(application) {


       val weatherData= MutableLiveData<WeatherData>();
       var weatherRepo:WeatherRepository

       init{
           weatherRepo = WeatherRepository(application)
       }

       public fun loadWeather(context: Context,lat:Double,lon:Double) : LiveData<List<WeatherData>> {
           return weatherRepo.fetchWeatherData(context,lat,lon)
       }

       public fun getWeatherObj() : LiveData<WeatherData> {
           return weatherData;
       }

}
