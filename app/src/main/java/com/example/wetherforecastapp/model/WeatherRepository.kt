package com.example.wetherforecastapp.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.local.LocalDataSource
import com.example.wetherforecastapp.model.remote.WeatherService
import kotlinx.coroutines.*

class WeatherRepository : ViewModel{
    var localDataSource: LocalDataSource
    var weatherService: WeatherService
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<Boolean>()

   constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherService
    }


    fun fetchWeatherData(context: Context,lat:Double,lon:Double) : LiveData<List<WeatherData>> {
        loadingLiveData.postValue(true)
       /* val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }*/
        if(isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    WeatherService.apiService.getCurrentWeatherByLatLng(lat, lon)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let { localDataSource.insert(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return  localDataSource.getData()
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun getWeatherData(): LiveData<List<WeatherData>> {
        return  localDataSource.getData()
    }
}