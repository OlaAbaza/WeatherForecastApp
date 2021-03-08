package com.example.wetherforecastapp.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData

class MainViewModel (application: Application) : AndroidViewModel(application) {

       var weatherRepo:WeatherRepository

       init{
           weatherRepo = WeatherRepository(application)
       }


       fun loadWeatherObj(context: Context, lat: Double, lon:Double, lang:String, unit:String) : LiveData<WeatherData>{
             weatherRepo.fetchWeatherObj(context,lat,lon,lang,unit)
             return weatherRepo.weatherObj
       }
       fun updateAllData(context: Context,lang: String, unit: String){
             weatherRepo.UpdateWeatherData(context, lang,unit)

        }
       fun getApiObj(timeZone: String): WeatherData {
        return weatherRepo.getApiObj(timeZone)
       }

}
