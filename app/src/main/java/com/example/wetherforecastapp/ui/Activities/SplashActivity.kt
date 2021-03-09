package com.example.wetherforecastapp.ui.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.wetherforecastapp.R
import com.stephentuso.welcome.WelcomeHelper
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    val activityScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.i("splash","out")
        activityScope.launch {
            delay(2000)
            Log.i("splash","in")
            var intent = Intent(this@SplashActivity, ScrollingActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}