package com.example.wetherforecastapp.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.wetherforecastapp.model.entity.Alarm
import com.example.wetherforecastapp.model.entity.WeatherData

class LocalDataSource {
    var weatherDao: WeatherDao


    constructor(application: Application) {
        weatherDao = WeatherDatabase.getDatabase(application).weatherDao()
    }

    fun getData(): LiveData<List<WeatherData>> {
        return weatherDao.getWeatherData()
    }
    fun getWeatherByLatLon(lat:Double,lon:Double): WeatherData{
        return weatherDao.getWeatherByLatLon(lat,lon)
    }


    fun getAll(): List<WeatherData> {
        return weatherDao.getAllData()
    }

    suspend fun insert(weatherData: WeatherData) {
        weatherDao.insert(weatherData)
    }

    suspend fun deleteWeatherObj(timeZone: String) {
        weatherDao.deleteWeatherObj(timeZone)
    }

    fun getApiObj(timeZone: String): WeatherData {
        return weatherDao.getObjByTimezone(timeZone)
    }

    //////Alarm///////////
    suspend fun deleteAlarmObj(id: Int) {
        weatherDao.deleteAlarmObj(id)
    }

    fun getAllAlarmObj(): LiveData<List<Alarm>> {
        return weatherDao.getAllAlarms()
    }

    suspend fun insertAlarm(alarmObj: Alarm): Long {
        return weatherDao.insertAlarm(alarmObj)
    }
}
