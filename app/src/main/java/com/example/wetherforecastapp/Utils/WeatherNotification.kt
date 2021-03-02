package com.example.wetherforecastapp.Utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.wetherforecastapp.R

class WeatherNotification(base: Context?) : ContextWrapper(base) {
    var mManager: NotificationManager? = null
    val ANDROID_CHANNEL_ID = "com.chikeandroid.tutsplustalerts.ANDROID"
    val ANDROID_CHANNEL_NAME = "ANDROID CHANNEL"

    init {
        createChannels()
    }

    fun createChannels() {
        // create android channel
        var androidChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            androidChannel = NotificationChannel(
                    ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            androidChannel.enableLights(true)
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true)
            // Sets the notification light color for notifications posted to this channel
            androidChannel.lightColor = Color.GREEN
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getManager()?.createNotificationChannel(androidChannel)
        }
    }

    fun getManager(): NotificationManager? {
        if (mManager == null) {
            mManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        }
        return mManager
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getAndroidChannelNotification( title: String,body: String,sound:Boolean): NotificationCompat.Builder? {
        if (sound){
            return NotificationCompat.Builder(
                    getApplicationContext(),ANDROID_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(false)
        }else{
            return NotificationCompat.Builder(
                    getApplicationContext(),ANDROID_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(false)
        }
    }
}