package com.example.wetherforecastapp.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    public fun loadWeather(context: Context, lat:Double, lon:Double,lang:String,unit:String) : LiveData<List<WeatherData>> {
        return apiRepository.fetchWeatherData(context,lat, lon,lang,unit)
    }
    public fun getWeatherList() : LiveData<List<WeatherData>> {
        return apiRepository.getWeatherData()
    }
    public fun getWeatherObj() : LiveData<WeatherData> {
        return apiObj;
    }
    public fun deleteWeatherObj(timeZone: String)  {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteWeatherObj(timeZone)
        }
    }
    fun delObj(): LiveData<String> {
        return deleteListener
    }
    fun showObj(): LiveData<WeatherData> {
        return displayListener
    }
    public fun onRemoveClick(timeZone:String)  {
        deleteListener.value=timeZone

    }
    public fun onShowClick(weatherObj:WeatherData){
        displayListener.value=weatherObj
    }


}