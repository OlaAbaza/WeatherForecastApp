package com.example.wetherforecastapp.ViewModels

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

class FavViewModel(application: Application) : AndroidViewModel(application) {

    val apiObj= MutableLiveData<WeatherData>();
    var apiRepository: WeatherRepository
    val deleteListener = MutableLiveData<String>()
    val displayListener = MutableLiveData<WeatherData>()
    var localDataSource : LocalDataSource

    init{
        apiRepository = WeatherRepository(application)
        localDataSource = LocalDataSource(application)
    }

     fun loadWeather(context: Context, lat:Double, lon:Double,lang:String,unit:String) : LiveData<List<WeatherData>> {
        return apiRepository.fetchWeatherData(context,lat, lon,lang,unit)
    }
     fun getWeatherList() : LiveData<List<WeatherData>> {
        return apiRepository.getWeatherData()
    }
     fun getWeatherObj() : LiveData<WeatherData> {
        return apiObj;
    }
     fun deleteWeatherObj(timeZone: String)  {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteWeatherObj(timeZone)
        }
    }
     fun insertWeatherObj(item: WeatherData)  {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.insert(item)
        }
    }
    fun delObj(): LiveData<String> {
        return deleteListener
    }
    fun showObj(): LiveData<WeatherData> {
        return displayListener
    }
     fun onRemoveClick(timeZone:String)  {
        deleteListener.value=timeZone

    }
     fun onShowClick(weatherObj:WeatherData){
        displayListener.value=weatherObj
    }


}