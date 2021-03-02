package com.example.wetherforecastapp.View

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforecastapp.MapsActivity
import com.example.wetherforecastapp.ViewModel.FavViewModel
import com.example.wetherforecastapp.databinding.ActivityFavBinding
import com.example.wetherforecastapp.databinding.FavDialogBinding
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.remote.Setting
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class FavoriteActivity : AppCompatActivity() {
    private lateinit var viewModel: FavViewModel
    lateinit var binding: ActivityFavBinding
    lateinit var bindingDialog: FavDialogBinding
    lateinit var favoriteAdapter : FavoriteAdapter
    var dailyListAdapter = DailyListAdapter(arrayListOf())
    var hourlyListAdapter = HourlyListAdapter(arrayListOf())
    lateinit var prefs: SharedPreferences

    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(
            FavViewModel::class.java
        )
        favoriteAdapter= FavoriteAdapter(arrayListOf(), viewModel, applicationContext)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("id")) {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var unit=prefs.getString("UNIT_SYSTEM", Setting.IMPERIAL.Value).toString()
            var lang=prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
            observeViewModel(viewModel, lat, lon, lang,unit)
            Toast.makeText(
                this, " " + lon + lat,
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            getWeatherData(viewModel)
        }
        initUI()
       binding.addBtn.setOnClickListener{
           val intent: Intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("mapId", 1)
            startActivity(intent)
            finish()
        }
        viewModel.delObj().observe(this, Observer<String> { timeZone ->
            viewModel.deleteWeatherObj(timeZone)
        })

        viewModel.showObj().observe(this, Observer<WeatherData> {
            showDialog(it);
            Toast.makeText(this, "you have arrived" + it.timezone, Toast.LENGTH_SHORT).show()
        })


    }
    private fun initUI() {
        binding.rvFavList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoriteAdapter

        }

    }

    private fun observeViewModel(
        viewModel: FavViewModel,
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ) {
            viewModel.loadWeather(applicationContext, lat, lon, lang, unit).observe(this, {
                favoriteAdapter.updateHours(it)
            })

    }
    private fun getWeatherData(viewModel: FavViewModel) {
        viewModel.getWeatherList().observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }
    private fun showDialog(weatherObj: WeatherData){
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingDialog = FavDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        initDialog()
        updateDialog(weatherObj)
        bindingDialog.closeBtn.setOnClickListener{
            dialog.dismiss()
        }
        Log.i("ola", "dialog " + weatherObj)
        dialog.show()
    }
    private fun updateDialog(item: WeatherData) {
        item?.let {
                item.apply {
                    Log.i("ola", "up ")
                    bindingDialog.dialogContent.homeTemp.text= currentWether.temp.toInt().toString()+"°"
                    bindingDialog.dialogContent.homeDesc.text= currentWether.weather.get(0).description.toString()
                    bindingDialog.dialogContent.homeCountry.text= timezone
                    bindingDialog.dialogContent.toolbarLayout.title=timezone
                    bindingDialog.dialogContent.iContent.HumidityVal.text=currentWether.humidity.toString()
                    bindingDialog.dialogContent.iContent.WindSpeedVal.text=currentWether.windSpeed.toString()
                    bindingDialog.dialogContent.iContent.PressureVal.text=currentWether.pressure.toString()
                    bindingDialog.dialogContent.iContent.CloudsVal.text=currentWether.clouds.toString()
                    bindingDialog.dialogContent.iContent.SunriseVal.text=timeFormat(currentWether.sunrise)
                    bindingDialog.dialogContent.iContent.SunsetVal.text=timeFormat(currentWether.sunset)
                    bindingDialog.dialogContent.homedata.text= dateFormat(currentWether.dt)
                    dailyListAdapter.updateDaily(daily)
                    hourlyListAdapter.updateHours(hourly)
                }
            }
        }
    private fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }
    private fun dateFormat(milliSeconds: Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month +year

    }

    fun initDialog(){
        bindingDialog.dialogContent.iContent.rvHourly.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyListAdapter

        }
        bindingDialog.dialogContent.iContent.rvDaily.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }
}