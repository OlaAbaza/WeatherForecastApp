package com.example.wetherforecastapp.model.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wetherforecastapp.model.entity.Alarm
import com.example.wetherforecastapp.model.entity.WeatherData

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather")
    fun getWeatherData(): LiveData<List<WeatherData>>

    @Query("SELECT * FROM weather")
    fun getAllData(): List<WeatherData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: WeatherData)

    @Delete
    suspend fun delete(weatherData: WeatherData): Void

    @Query("DELETE FROM weather")
    suspend fun deleteAll()

    @Query("select * From weather where timezone =:mTimeZone")
    fun getObjByTimezone(mTimeZone: String): WeatherData

    @Query("DELETE FROM weather WHERE timezone = :timezone")
    suspend fun deleteWeatherObj(timezone:String)

    //////////////Alarm/////////////////
    @Query("SELECT * FROM Alarms")
    fun getAllAlarms(): LiveData<List<Alarm>>

    @Query("SELECT * FROM Alarms Where id = :id ")
    fun getApiObj(id:Int): Alarm

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmObj: Alarm):Long

    @Query("DELETE FROM Alarms WHERE id = :id")
    suspend fun deleteAlarmObj(id:Int)
}