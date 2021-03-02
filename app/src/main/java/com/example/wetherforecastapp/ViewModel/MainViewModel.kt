package com.example.wetherforecastapp.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel (application: Application) : AndroidViewModel(application) {

       var weatherRepo:WeatherRepository

       init{
           weatherRepo = WeatherRepository(application)
       }

      public fun loadWeather(context: Context,lat:Double,lon:Double,lang:String,unit:String) : LiveData<List<WeatherData>> {
           return weatherRepo.fetchWeatherData(context,lat,lon,lang,unit)
       }

      public fun loadWeatherObj(context: Context, lat: Double, lon:Double, lang:String, unit:String) : LiveData<WeatherData>{
             weatherRepo.fetchWeatherObj(context,lat,lon,lang,unit)
             return weatherRepo.weatherObj
       }
      public fun updateAllData(context: Context,lang: String, unit: String){
             weatherRepo.UpdateWeatherData(context, lang,unit)

        }
      public fun getApiObj(timeZone: String): WeatherData {
        return weatherRepo.getApiObj(timeZone)
       }

}
