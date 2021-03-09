package com.example.wetherforecastapp.ui.Activities

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.wetherforecastapp.model.remote.Setting
import java.util.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var mCurrentLocale: Locale
    private var lang: String = ""
    private var isUpdated: Boolean = false
    override fun onStart() {
        super.onStart()
        mCurrentLocale = resources.configuration.locale
        Log.i("lang", "onStart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCurrentLocale = resources.configuration.locale
        val locale: Locale = getLocale(this)
        if (!locale.equals(mCurrentLocale) || isUpdated) {
            // mCurrentLocale = locale
            setLocale()
            Log.i("lang", "onCreate")
        }
    }

    override fun onRestart() {
        super.onRestart()
        val locale: Locale = getLocale(this)
        if (!locale.equals(mCurrentLocale) || isUpdated) {
            // mCurrentLocale = locale
            setLocale()
            recreate()
            Log.i("lang", "onRestart")
        }
    }

    private fun setLocale() {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())

    }

    fun getLocale(context: Context): Locale {
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        lang = sharedPreferences.getString("APP_LANG", Setting.ENGLISH.Value).toString()
        isUpdated = sharedPreferences.getBoolean("isUpdated", false)
        Log.i("lang", lang + " " + isUpdated)
        return Locale(lang)
    }

}