package com.example.wetherforecastapp.View

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforecastapp.ViewModel.MainViewModel
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.databinding.ActivityScrollingBinding
import com.example.wetherforecastapp.model.entity.WeatherData
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
       // setSupportActionBar(binding.toolbar)
       // findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = ""
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
    viewModel.loadWeather().observe(this, { updateList(it) })

    }
    private fun dateFormatter( milliSeconds:Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong()*1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month +year

    }
    private fun updateList(items: List<WeatherData>?) {
        items?.let {
            for (item in items){
                  item.apply {
                      binding.homeTemp.text= currentWether.temp.toInt().toString()+"°"
                      binding.homeDesc.text= currentWether.weather.get(0).description.toString()
                      binding.homeCountry.text= timezone
                      binding.homedata.text= dateFormatter(currentWether.dt)
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}