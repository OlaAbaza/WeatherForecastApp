package com.example.wetherforecastapp.ViewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData

class MainViewModel (application: Application) : AndroidViewModel(application) {


       val weatherData= MutableLiveData<WeatherData>();
       lateinit var weatherRepo:WeatherRepository

       init{
           weatherRepo = WeatherRepository(application)
       }

       public fun loadWeather() : LiveData<List<WeatherData>> {
           return weatherRepo.fetchWeatherData()
       }

       public fun getWeatherObj() : LiveData<WeatherData> {
           return weatherData;
       }

}
