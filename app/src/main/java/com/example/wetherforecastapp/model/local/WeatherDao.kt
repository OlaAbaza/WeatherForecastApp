package com.example.wetherforecastapp.model.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wetherforecastapp.model.entity.WeatherData

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather")
    fun getWeatherData(): LiveData<List<WeatherData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: WeatherData)

    @Delete
    suspend fun delete(weatherData: WeatherData): Void

    @Query("DELETE FROM weather")
    suspend fun deleteAll()

    @Query("select * From weather where TimeZone =:mTimeZone")
    fun getweatherByTime(mTimeZone: String): LiveData<WeatherData>
}