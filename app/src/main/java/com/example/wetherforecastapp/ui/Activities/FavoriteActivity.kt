package com.example.wetherforecastapp.ui.Activities

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.ui.adapters.DailyListAdapter
import com.example.wetherforecastapp.ui.adapters.FavoriteAdapter
import com.example.wetherforecastapp.ui.adapters.HourlyListAdapter
import com.example.wetherforecastapp.ViewModels.FavViewModel
import com.example.wetherforecastapp.databinding.ActivityFavBinding
import com.example.wetherforecastapp.databinding.FavDialogBinding
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.remote.Setting
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class FavoriteActivity : AppCompatActivity() {
    private lateinit var viewModel: FavViewModel
    private lateinit var binding: ActivityFavBinding
    private lateinit var bindingDialog: FavDialogBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var dailyListAdapter = DailyListAdapter(arrayListOf())
    private var hourlyListAdapter = HourlyListAdapter(arrayListOf())
    private lateinit var prefs: SharedPreferences
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(
                FavViewModel::class.java
        )
        favoriteAdapter = FavoriteAdapter(arrayListOf(), viewModel, applicationContext)
        initUI()
        rvSwipeListener()
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)


        if (intent.hasExtra("id")) {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var unit = prefs.getString("UNIT_SYSTEM", Setting.IMPERIAL.Value).toString()
            var lang = prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
            observeViewModel(viewModel, lat, lon, lang, unit)
            Toast.makeText(this, " " + lon + lat, Toast.LENGTH_SHORT).show()
        } else {
            getWeatherData(viewModel)
        }

        binding.addBtn.setOnClickListener {
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
        })


    }

    private fun initUI() {
        binding.rvFavList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoriteAdapter

        }

    }

    private fun observeViewModel(viewModel: FavViewModel, lat: Double, lon: Double, lang: String, unit: String) {
        viewModel.loadWeather(applicationContext, lat, lon, lang, unit).observe(this, {
            favoriteAdapter.updateHours(it)
        })

    }

    private fun getWeatherData(viewModel: FavViewModel) {
        viewModel.getWeatherList().observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }

    private fun showDialog(weatherObj: WeatherData) {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bindingDialog = FavDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        initDialog()
        updateDialog(weatherObj)
        bindingDialog.closeBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateDialog(item: WeatherData) {
        item?.let {
            item.apply {
                var sunrise = timeFormat(currentWether.sunrise)
                var sunset = timeFormat(currentWether.sunset)
                var split_timezone = timezone.split("/")
                bindingDialog.favTemb.text = currentWether.temp.toInt().toString() + "°"
                bindingDialog.timezoneTxt.text = split_timezone[0] + "\n" + split_timezone[1]
                bindingDialog.favDesc.text = currentWether.weather.get(0).description.toString()

                when {
                    currentWether.weather.get(0).icon.contains("02d" + "", ignoreCase = true) -> bindingDialog.decIcon.setImageResource(R.drawable.cloud_sun)
                    currentWether.weather.get(0).icon.contains("09" + "", ignoreCase = true)||  currentWether.weather.get(0).icon.contains("10" + "", ignoreCase = true) -> bindingDialog.decIcon.setImageResource(R.drawable.rain)
                    currentWether.weather.get(0).icon.contains("13" + "", ignoreCase = true) -> bindingDialog.decIcon.setImageResource(R.drawable.snow)
                    currentWether.weather.get(0).icon.contains("02n" + "", ignoreCase = true) -> bindingDialog.decIcon.setImageResource(R.drawable.cloud_night)
                    currentWether.weather.get(0).icon.contains("01d" + "", ignoreCase = true)  -> bindingDialog.decIcon.setImageResource(R.drawable.sun)
                    currentWether.weather.get(0).icon.contains("01n" + "", ignoreCase = true)  -> bindingDialog.decIcon.setImageResource(R.drawable.night_icon)
                    currentWether.weather.get(0).icon.contains("11" + "", ignoreCase = true)  -> bindingDialog.decIcon.setImageResource(R.drawable.ic_wind)
                    else -> bindingDialog.decIcon.setImageResource(R.drawable.ic_cloud1)
                }
                bindingDialog.dialogContent.HumidityVal.text = currentWether.humidity.toString()
                bindingDialog.dialogContent.WindSpeedVal.text = currentWether.windSpeed.toString()
                bindingDialog.dialogContent.PressureVal.text = currentWether.pressure.toString()
                bindingDialog.dialogContent.CloudsVal.text = currentWether.clouds.toString()
                bindingDialog.dialogContent.SunriseVal.text = sunrise
                bindingDialog.dialogContent.SunsetVal.text = sunset
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

    fun initDialog() {
        bindingDialog.dialogContent.rvHourly.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyListAdapter

        }
        bindingDialog.dialogContent.rvDaily.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }

    private fun rvSwipeListener() {
        val itemTouchHelperCallback =
                object :
                        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                    ): Boolean {

                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val item: WeatherData = favoriteAdapter.getItemAt(position)
                        viewModel.onRemoveClick(item.timezone)
                        favoriteAdapter.notifyItemRemoved(position);
                        Toast.makeText(
                                applicationContext, "deleted",
                                Toast.LENGTH_SHORT
                        ).show()
                        Snackbar.make(
                                binding.favLayout, // The ID of your coordinator_layout
                                "Deleted",
                                Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("UNDO") {
                                viewModel.insertWeatherObj(item)
                                binding.rvFavList.scrollToPosition(position)
                            }
                            setTextColor(Color.parseColor("#504FD3"))
                            setActionTextColor(Color.parseColor("#504FD3"))
                            setBackgroundTint(Color.parseColor("#D5D1D1"))
                            duration.minus(1)
                        }.show()
                    }

                }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvFavList)
    }
}