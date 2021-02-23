package com.example.wetherforecastapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.wetherforecastapp.model.remote.Resource
import com.example.wetherforecastapp.model.remote.WetherApi
import kotlinx.coroutines.Dispatchers








class MainViewModel(private val mainRepository: WetherApi) : ViewModel() {

    fun getWeather() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCurrentWeatherByLatLng(33.441792,-94.037689)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}