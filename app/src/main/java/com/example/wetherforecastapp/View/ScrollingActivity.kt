package com.example.wetherforecastapp.View

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforecastapp.FavoriteActivity
import com.example.wetherforecastapp.ViewModel.MainViewModel
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.SettingsActivity
import com.example.wetherforecastapp.databinding.ActivityScrollingBinding
import com.example.wetherforecastapp.model.entity.WeatherData
import java.text.SimpleDateFormat
import java.util.*

class ScrollingActivity : AppCompatActivity() {
    lateinit var binding:ActivityScrollingBinding
    private lateinit var viewModel: MainViewModel
    var dailyListAdapter = DailyListAdapter(arrayListOf())
    var hourlyListAdapter = HourlyListAdapter(arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
       viewModel = ViewModelProviders.of(this,
           ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(
           MainViewModel::class.java)
           setSupportActionBar(binding.toolbar)
         binding.toolbarLayout.title=" "
       // findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = theme.applyStyle()
        initUI()
        observeViewModel(viewModel)
    }
    private fun initUI() {
        binding.iContent.rvHourly.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = hourlyListAdapter

        }
        binding.iContent.rvDaily.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }

    private fun observeViewModel(viewModel: MainViewModel) {
        //viewModel.loadingLiveData.observe(this, { showLoading(it) })
       // viewModel.errorLiveData.observe(this, { showError(it) })
    viewModel.loadWeather(applicationContext,33.441792,-94.037689).observe(this, { updateList(it) })

    }
    private fun dateFormat( milliSeconds:Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong()*1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month +year

    }
    private fun timeFormat(millisSeconds:Int ): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }
    private fun updateList(items: List<WeatherData>?) {
        items?.let {
            for (item in items){
                  item.apply {
                      binding.homeTemp.text= currentWether.temp.toInt().toString()+"°"
                      binding.homeDesc.text= currentWether.weather.get(0).description.toString()
                      binding.homeCountry.text= timezone
                      binding.toolbarLayout.title=timezone
                      binding.iContent.HumidityVal.text=currentWether.humidity.toString()
                      binding.iContent.WindSpeedVal.text=currentWether.windSpeed.toString()
                      binding.iContent.PressureVal.text=currentWether.pressure.toString()
                      binding.iContent.CloudsVal.text=currentWether.clouds.toString()
                      binding.iContent.SunriseVal.text=timeFormat(currentWether.sunrise)
                      binding.iContent.SunsetVal.text=timeFormat(currentWether.sunset)
                      binding.homedata.text= dateFormat(currentWether.dt)
                      dailyListAdapter.updateDaily(daily)
                      hourlyListAdapter.updateHours(hourly)
                  }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings ->{
                val intent: Intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_fav -> {
                val intent: Intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}