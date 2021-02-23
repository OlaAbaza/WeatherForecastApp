package com.example.wetherforecastapp.model.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather")
data class WeatherData(
    @SerializedName("current")
    @Embedded
    val currentWether: CurrentWether,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    @PrimaryKey
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    @SerializedName("alerts")
    val alerts: List<Alert>
)