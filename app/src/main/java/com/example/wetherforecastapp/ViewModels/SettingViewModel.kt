package com.example.wetherforecastapp.ViewModels

import android.app.Application
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import com.example.wetherforecastapp.Utils.WeatherNotification
import com.example.wetherforecastapp.model.WeatherRepository
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    var localDataSource: LocalDataSource
    lateinit var apiObj: WeatherData

    init {
        localDataSource = LocalDataSource(application)
    }

    fun notifyUser(timeZone: String) {
        var jop = CoroutineScope(Dispatchers.IO).launch {
            apiObj = localDataSource.getApiObj(timeZone)
        }
        jop.invokeOnCompletion {
            var notificationUtils = WeatherNotification(getApplication())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nb: NotificationCompat.Builder? =
                    notificationUtils.getAndroidChannelNotification(
                        "" + apiObj.currentWether.temp.toInt()
                            .toString() + "°" + " " + apiObj.timezone,
                        "" + apiObj.currentWether.weather.get(0).description,
                        true,
                        true
                    )
                notificationUtils.getManager()?.notify(4, nb?.build())
            }

        }
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(getApplication()).cancel(null, 4);
    }
}