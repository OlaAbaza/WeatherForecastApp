package com.example.wetherforecastapp

import android.app.Application
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.remote.Resource
import com.example.wetherforecastapp.model.remote.WeatherService
import com.example.wetherforecastapp.model.remote.WetherApi
import kotlinx.coroutines.Dispatchers

class MainViewModel (application: Application) : AndroidViewModel(application) {

   /* fun getWeather() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCurrentWeatherByLatLng(33.441792,-94.037689)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }*/

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
