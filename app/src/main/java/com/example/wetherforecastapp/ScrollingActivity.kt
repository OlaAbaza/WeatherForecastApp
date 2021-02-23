package com.example.wetherforecastapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wetherforecastapp.databinding.ActivityScrollingBinding
import com.example.wetherforecastapp.model.remote.Status
import com.example.wetherforecastapp.model.remote.WeatherService

class ScrollingActivity : AppCompatActivity() {
    lateinit var binding:ActivityScrollingBinding
    private lateinit var viewModel: MainViewModel
    //private lateinit var adapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_scrolling)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // setSupportActionBar(binding.toolbar)
       // findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = ""
        setupViewModel()
        setupObservers()
      /*  val response=WetherApi()
         GlobalScope.launch (Dispatchers.Main){
            val currentWeather = response.getCurrentWeatherByLatLng(33.441792,-94.037689)
             binding.mainTemperature.text=currentWeather.toString()
             Log.i("ola",currentWeather.toString())
         }*/
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

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(WeatherService.apiService)
        ).get(MainViewModel::class.java)
    }

    /*private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(arrayListOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }*/

    private fun setupObservers() {
        viewModel.getWeather().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        //recyclerView.visibility = View.VISIBLE
                        //progressBar.visibility = View.GONE
                        //resource.data?.let { forecast -> retrieveList(users) }
                        Log.i("ola",   "resource.data?.currentWether.toString()")
                        Log.i("ola",   resource.data?.currentWether.toString())
                        resource.data?.let{
                            Log.i("ola",   resource.data?.currentWether.toString())
                        }

                    }
                    Status.ERROR -> {
                        //recyclerView.visibility = View.VISIBLE
                        //progressBar.visibility = View.GONE
                        Log.i("ola",   it.message.toString())
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        //progressBar.visibility = View.VISIBLE
                        //recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

   /* private fun retrieveList(users: List<User>) {
        adapter.apply {
            addUsers(users)
            notifyDataSetChanged()
        }
    }
}*/
}