package com.example.wetherforecastapp.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.local.LocalDataSource
import com.example.wetherforecastapp.model.remote.Resource
import com.example.wetherforecastapp.model.remote.WeatherService
import kotlinx.coroutines.*

class WeatherRepository : ViewModel{
    lateinit var localDataSource: LocalDataSource
    lateinit var weatherService: WeatherService
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<Boolean>()
    val WeatherLiveData = MutableLiveData<WeatherData>()

   constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherService
    }


    fun fetchWeatherData() : LiveData<List<WeatherData>> {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            val response = WeatherService.apiService.getCurrentWeatherByLatLng(33.441792,-94.037689)
                if (response.isSuccessful) {
                    response.body()?.let { localDataSource.insert(it) }

                }
        }
        return  localDataSource.getData()
    }

  /*  fun getWeather() {
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
    }*/
}