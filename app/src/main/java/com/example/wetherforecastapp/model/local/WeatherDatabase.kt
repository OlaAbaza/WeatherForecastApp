package com.example.wetherforecastapp.model.local

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wetherforecastapp.model.entity.Alarm
import com.example.wetherforecastapp.model.entity.WeatherData

@Database(entities = arrayOf(WeatherData::class,Alarm::class), version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao():WeatherDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(application: Application): WeatherDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    WeatherDatabase::class.java,
                    "weatherDB"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}