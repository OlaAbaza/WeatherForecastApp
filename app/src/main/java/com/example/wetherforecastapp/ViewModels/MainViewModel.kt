package com.example.wetherforecastapp.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var weatherRepo: WeatherRepository

    init {
        weatherRepo = WeatherRepository(application)
    }


    fun loadWeatherObj(
        context: Context,
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): LiveData<WeatherData> {
        weatherRepo.fetchWeatherObj(context, lat, lon, lang, unit)
        return weatherRepo.weatherObj
    }

    fun updateAllData(context: Context, lang: String, unit: String) {
        weatherRepo.UpdateWeatherData(context, lang, unit)

    }

    fun getApiObj(timeZone: String): WeatherData {
        return weatherRepo.getApiObj(timeZone)
    }

    fun getloading() = weatherRepo.loadingLiveData


    fun dateFormat(milliSeconds: Int): String {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year = calendar.get(Calendar.YEAR).toString()
        return day + month + year

    }

    fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }

}
