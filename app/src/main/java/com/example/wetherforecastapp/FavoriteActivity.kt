package com.example.wetherforecastapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforecastapp.View.HourlyListAdapter
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.databinding.ActivityFavBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoriteActivity : AppCompatActivity() {
    private lateinit var viewModel: FavViewModel
    lateinit var binding: ActivityFavBinding
    private lateinit var addBtn: FloatingActionButton
    lateinit var favoriteAdapter : FavoriteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
            FavViewModel::class.java)
        favoriteAdapter=FavoriteAdapter(arrayListOf(),viewModel,applicationContext)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("id")) {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            observeViewModel(viewModel,lat,lon)
            Toast.makeText(
                this,  " " + lon+lat,
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            getWeatherData(viewModel)
        }

       binding.addBtn.setOnClickListener{
            val intent: Intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
       /* viewModel.getNavigate().observe(this, Observer<String> { timeZone ->
            viewModel.deleteApiObj(timeZone)
        })*/
        initUI()

    }

    private fun initUI() {
        binding.rvFavList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = favoriteAdapter

        }
    }

    private fun observeViewModel(viewModel: FavViewModel, lat:Double, lon:Double) {
        viewModel.loadWeather(applicationContext,lat, lon).observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }
    private fun getWeatherData(viewModel: FavViewModel) {
        viewModel.getWeatherList().observe(this, {
            favoriteAdapter.updateHours(it)
        })
    }

    private fun updateList(it: List<WeatherData>) {

    }
}