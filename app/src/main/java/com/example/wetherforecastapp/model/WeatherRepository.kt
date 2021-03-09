package com.example.wetherforecastapp.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.local.LocalDataSource
import com.example.wetherforecastapp.model.remote.WeatherService
import kotlinx.coroutines.*

class WeatherRepository : ViewModel {
    var localDataSource: LocalDataSource
    var weatherService: WeatherService
    var weatherObj = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()

    constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherService
    }

    fun fetchWeatherData(
        context: Context,
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): LiveData<List<WeatherData>> {
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    WeatherService.apiService.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let { localDataSource.insert(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return localDataSource.getData()
    }

    fun fetchWeatherObj(context: Context, lat: Double, lon: Double, lang: String, unit: String) {
        loadingLiveData.postValue(true)
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    WeatherService.apiService.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            localDataSource.insert(it)
                            weatherObj.postValue(it)
                        }
                        loadingLiveData.postValue(false)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun UpdateWeatherData(context: Context, lang: String, unit: String) {
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                var weatherData = localDataSource.getAll()
                for (item in weatherData) {
                    val response =
                        WeatherService.apiService.getCurrentWeatherByLatLng(
                            item.lat,
                            item.lon,
                            lang,
                            unit
                        )
                    try {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                localDataSource.insert(it)
                                Log.i("ola", "Up")
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    fun getWeatherData(): LiveData<List<WeatherData>> {
        return localDataSource.getData()
    }

    fun getApiObj(timeZone: String): WeatherData {
        return localDataSource.getApiObj(timeZone)
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


}