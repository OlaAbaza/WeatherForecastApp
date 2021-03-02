package com.example.wetherforecastapp.View

import android.Manifest
import android.provider.Settings
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.SettingsActivity
import com.example.wetherforecastapp.Utils.WeatherNotification
import com.example.wetherforecastapp.ViewModel.MainViewModel
import com.example.wetherforecastapp.Utils.WorkManger
import com.example.wetherforecastapp.databinding.ActivityScrollingBinding
import com.example.wetherforecastapp.model.entity.Alert
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.remote.Setting
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ScrollingActivity : AppCompatActivity()  {
    lateinit var binding:ActivityScrollingBinding
    private lateinit var viewModel: MainViewModel
    lateinit var notificationUtils: WeatherNotification
    var yourLocationLat:Double=0.0
    var yourLocationLon:Double=0.0
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var prefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var dailyListAdapter = DailyListAdapter(arrayListOf())
    var hourlyListAdapter = HourlyListAdapter(arrayListOf())
    var lat: String=""
    var timezone: String=""
    var lon: String=""
    var loc:Boolean=true
    var isUpdated:Boolean=false
    var lang:String=""
    var unit:String=""

    var handler = Handler(Handler.Callback {
        Toast.makeText(applicationContext,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor.putString("lat", yourLocationLat.toString())
        editor.putString("lon", yourLocationLon.toString())
        editor.commit()
        lon=prefs.getString("lon", "").toString()
        lat= prefs.getString("lat", "").toString()
        observeViewModel(viewModel)
        true
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor= prefs.edit()
        viewModel = ViewModelProviders.of(this,
               ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(
               MainViewModel::class.java)
           setSupportActionBar(binding.toolbar)
         binding.toolbarLayout.title=" "

       //findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = theme.applyStyle()
        val saveRequest =
                PeriodicWorkRequest.Builder(WorkManger::class.java,15, TimeUnit.MINUTES)
                        .addTag("up")
                        .build()
        WorkManager
                .getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                        "up",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        saveRequest)
        initUI()

        loc=prefs.getBoolean("USE_DEVICE_LOCATION", true)
        unit=prefs.getString("UNIT_SYSTEM", Setting.METRIC.Value).toString()
        lang=prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
        lon=prefs.getString("lon", ("")).toString()
        lat=prefs.getString("lat", ("")).toString()
        timezone=prefs.getString("timezone", ("")).toString()
        if(!timezone.isNullOrEmpty()) {
            getObjByTimezone()
        }

        if(loc){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
        }
        else{
            observeViewModel(viewModel)
        }

    }

    private fun getObjByTimezone() {
       Log.i("ola", "timezone"+timezone)
        CoroutineScope(Dispatchers.IO).launch {
           var weather= viewModel.getApiObj(timezone)
            updateObj(weather)
        }
    }

    //-24.7847,-65.4315
    private fun initUI() {
        binding.iContent.rvHourly.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyListAdapter

        }
        binding.iContent.rvDaily.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = dailyListAdapter
        }
    }
    private fun observeViewModel(viewModel: MainViewModel) {
        Log.i("ola", "location:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
        viewModel.loadWeatherObj(applicationContext, lat.toDouble(), lon.toDouble(), lang, unit).observe(this, {updateObj(it)})
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
    private fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }

    private fun updateObj(item: WeatherData) {
        item?.let {
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
            item.alerts?.let {
                notifyUser(it)
            }

            editor.putString("timezone", item.timezone)
            editor.commit()
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
            R.id.action_settings -> {

                val intent: Intent = Intent(this, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                true
            }
            R.id.action_fav -> {
                val intent: Intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_Alerts -> {
                val intent: Intent = Intent(this,AlarmActivity::class.java)
                startActivity(intent)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun notifyUser(alert:List<Alert>){
        notificationUtils = WeatherNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(alert.get(0)?.event, ""
                    +dateFormat(alert.get(0)?.start.toInt())+","+dateFormat(alert.get(0)?.end.toInt()) +"\n"+alert.get(0)?.description,true)
            notificationUtils.getManager()?.notify(3, nb?.build())
        }
    }
    ///////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //TODO
                        yourLocationLat=location.latitude
                        yourLocationLon=location.longitude
                        handler.sendEmptyMessage(0)
                        Toast.makeText(this,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            //TODO
            yourLocationLat=mLastLocation.latitude
            yourLocationLon=mLastLocation.longitude
            handler.sendEmptyMessage(0)
            Toast.makeText(applicationContext,"location:"+yourLocationLat+","+yourLocationLon,Toast.LENGTH_SHORT).show()
        }
    }
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
    ////////////////////////////////////////////////////

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }
    override fun onResume() {
        super.onResume()
        isUpdated=prefs.getBoolean("isUpdated", false)
        Log.i("ola"," "+isUpdated.toString()+"resume")
        if(isUpdated)
        {

            loc=prefs.getBoolean("USE_DEVICE_LOCATION", true)
            unit=prefs.getString("UNIT_SYSTEM", Setting.IMPERIAL.Value).toString()
            lang=prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
            lon=prefs.getString("lon", ("")).toString()
            lat=prefs.getString("lat", ("")).toString()
            setLocale(this,lang)
            if(loc){
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            }
            else{
                viewModel.loadWeatherObj(applicationContext,lat.toDouble(),lon.toDouble(),lang,unit)
            }
            Log.i("ola", "before res:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
            viewModel.updateAllData(applicationContext,lang,unit)
            Log.i("ola", "after  res:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
            editor.putBoolean("isUpdated", false)
            editor.commit()

        }

    }
    override fun onPause() {
        super.onPause()
        editor.putBoolean("isUpdated", false)
        editor.commit()
    }
}