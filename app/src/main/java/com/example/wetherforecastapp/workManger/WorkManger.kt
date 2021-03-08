package com.example.wetherforecastapp.workManger

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.remote.Setting


class WorkManger(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var weatherRepository : WeatherRepository = WeatherRepository(appContext.applicationContext as Application)
    var prefs= PreferenceManager.getDefaultSharedPreferences(applicationContext)
    override fun doWork(): Result {
        return try {
            // Do the work here--in this case, upload the images.
            var unit = prefs.getString("UNIT_SYSTEM", Setting.IMPERIAL.Value).toString()
            var lang = prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
            weatherRepository.UpdateWeatherData(applicationContext, lang, unit)
            Log.i("ola", "WorkMangerstart")
            // Indicate whether the work finished successfully with the Result
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}
