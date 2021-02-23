package com.example.wetherforecastapp.model.local

import androidx.room.TypeConverter
import com.example.wetherforecastapp.model.entity.Alert
import com.example.wetherforecastapp.model.entity.Daily
import com.example.wetherforecastapp.model.entity.Hourly
import com.example.wetherforecastapp.model.entity.WeatherData
import com.google.gson.Gson




class Converters<T> {
        @TypeConverter
        fun <T> listToJson (value:   List<T>?) = Gson().toJson(value)

        @TypeConverter
        fun jsonToHourlyList(value: String) = Gson().fromJson(value, Array<Hourly>::class.java).toList()
        @TypeConverter
        fun jsonToDailyList(value: String) = Gson().fromJson(value, Array<Daily>::class.java).toList()
        @TypeConverter
        fun jsonToAlertList(value: String) = Gson().fromJson(value, Array<Alert>::class.java).toList()
}