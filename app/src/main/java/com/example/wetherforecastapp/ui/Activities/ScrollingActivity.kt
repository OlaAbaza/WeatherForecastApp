package com.example.wetherforecastapp.ui.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.Utils.WeatherNotification
import com.example.wetherforecastapp.ViewModels.MainViewModel
import com.example.wetherforecastapp.databinding.ActivityScrollingBinding
import com.example.wetherforecastapp.model.entity.Alert
import com.example.wetherforecastapp.model.entity.WeatherData
import com.example.wetherforecastapp.model.remote.Setting
import com.example.wetherforecastapp.ui.adapters.DailyListAdapter
import com.example.wetherforecastapp.ui.adapters.HourlyListAdapter
import com.example.wetherforecastapp.workManger.WorkManger
import com.google.android.gms.location.*
import com.stephentuso.welcome.WelcomeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class ScrollingActivity : BaseActivity() {
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var notificationUtils: WeatherNotification
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var yourLocationLat: Double = 0.0
    private var yourLocationLon: Double = 0.0
    private val PERMISSION_ID = 42
    private var dailyListAdapter = DailyListAdapter(arrayListOf())
    private var hourlyListAdapter = HourlyListAdapter(arrayListOf())
    private var lat: String = ""
    private var timezone: String = ""
    private var lon: String = ""
    private var lang: String = ""
    private var unit: String = ""
    private var loc: Boolean = true
    private var isUpdated: Boolean = false
    private var welcomeScreen: WelcomeHelper? = null

    var handler = Handler(Handler.Callback {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor.putString("lat", yourLocationLat.toString())
        editor.putString("lon", yourLocationLon.toString())
        editor.commit()
        lon = prefs.getString("lon", "").toString()
        lat = prefs.getString("lat", "").toString()
        observeViewModel(viewModel)
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        welcomeScreen = WelcomeHelper(this, WelcomeScreenActivity::class.java)
        welcomeScreen!!.show(savedInstanceState)

        checkWhiteListPermission()
        setUpWorkManger()
        initUI()
        iconsClickListener()

        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        editor = prefs.edit()
        viewModel = ViewModelProviders.of(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(
            MainViewModel::class.java
        )


        loc = prefs.getBoolean("USE_DEVICE_LOCATION", true)
        unit = prefs.getString("UNIT_SYSTEM", Setting.METRIC.Value).toString()
        lang = prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
        lon = prefs.getString("lon", ("")).toString()
        lat = prefs.getString("lat", ("")).toString()
        timezone = prefs.getString("timezone", ("")).toString()
        if (!timezone.isNullOrEmpty()) {
            getObjByTimezone()
        }
        if (loc) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
        } else {
            observeViewModel(viewModel)
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

    private fun iconsClickListener() {
        binding.apply {
            menuAlarm.setOnClickListener {
                val intent = Intent(applicationContext, AlarmActivity::class.java)
                startActivity(intent)
                //overridePendingTransition(R.anim.slide_out_left,  R.anim.slide_in_left);
            }
            menuFav.setOnClickListener {
                val intent = Intent(applicationContext, FavoriteActivity::class.java)
                startActivity(intent)
            }
            menuSetting.setOnClickListener {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
        }
    }

    private fun getObjByTimezone() {
        Log.i("ola", "timezone" + timezone)
       CoroutineScope(Dispatchers.IO).launch {
            var weather = viewModel.getApiObj(timezone)
            withContext(Dispatchers.Main) {
                updateObj(weather)
            }
        }
    }

    private fun observeViewModel(viewModel: MainViewModel) {
        Log.i("ola", "location:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
        viewModel.loadWeatherObj(applicationContext, lat.toDouble(), lon.toDouble(), lang, unit)
            .observe(this, { updateObj(it) })
        viewModel.getloading().observe(this, { showLoading(it) })
    }
    private fun showLoading(it: Boolean) {
        if (it) {
            binding.loadingView.visibility = View.VISIBLE
            binding.header.visibility = View.INVISIBLE
        } else {
            binding.header.visibility = View.VISIBLE
            binding.loadingView.visibility = View.GONE
        }
    }
    private fun updateObj(item: WeatherData) {
        item?.let {
            item.apply {
                binding.homeTemp.text = currentWether.temp.toInt().toString() + "°"
                binding.homeDesc.text = currentWether.weather.get(0).description.toString()
                binding.homeCountry.text = timezone
                binding.iContent.HumidityVal.text = currentWether.humidity.toString()
                binding.iContent.WindSpeedVal.text = currentWether.windSpeed.toString()
                binding.iContent.PressureVal.text = currentWether.pressure.toString()
                binding.iContent.CloudsVal.text = currentWether.clouds.toString()
                binding.iContent.SunriseVal.text = viewModel.timeFormat(currentWether.sunrise)
                binding.iContent.SunsetVal.text = viewModel.timeFormat(currentWether.sunset)
                binding.homedata.text = viewModel.dateFormat(currentWether.dt)
                binding.homeetime.text = viewModel.timeFormat(currentWether.dt)
                dailyListAdapter.updateDaily(daily)
                hourlyListAdapter.updateHours(hourly)
                when {
                    currentWether.weather.get(0).icon.contains(
                        "02d" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.cloud_sun)
                    currentWether.weather.get(0).icon.contains(
                        "09" + "",
                        ignoreCase = true
                    ) || currentWether.weather.get(0).icon.contains(
                        "10" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.rain)
                    currentWether.weather.get(0).icon.contains(
                        "13" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.snow)
                    currentWether.weather.get(0).icon.contains(
                        "02n" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.cloud_night)
                    currentWether.weather.get(0).icon.contains(
                        "01d" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.sun)
                    currentWether.weather.get(0).icon.contains(
                        "01n" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.night_icon)
                    currentWether.weather.get(0).icon.contains(
                        "11" + "",
                        ignoreCase = true
                    ) -> binding.decIcon.setImageResource(R.drawable.ic_wind)
                    else -> binding.decIcon.setImageResource(R.drawable.ic_cloud1)
                }

            }
            item.alerts?.let {
                notifyUser(it)
            }

            editor.putString("timezone", item.timezone)
            editor.commit()
        }

    }


    private fun notifyUser(alert: List<Alert>) {
        notificationUtils = WeatherNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nb: NotificationCompat.Builder? = notificationUtils.getAndroidChannelNotification(
                alert.get(0)?.event, ""
                        + viewModel.dateFormat(alert.get(0)?.start.toInt()) + "," + viewModel.dateFormat(alert.get(0)?.end.toInt()) + "\n" + alert.get(
                    0
                )?.description, true,false
            )
            notificationUtils.getManager()?.notify(3, nb?.build())
        }
    }

    fun setUpWorkManger() {
        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(false)
            .build()
        val saveRequest =
            PeriodicWorkRequest.Builder(WorkManger::class.java, 15, TimeUnit.MINUTES)
                .addTag("up")
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "up",
            ExistingPeriodicWorkPolicy.REPLACE, saveRequest
        )
    }

    ////////////////////////current location///////////////////////////////
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
                        yourLocationLat = location.latitude
                        yourLocationLon = location.longitude
                        handler.sendEmptyMessage(0)
                        //  Toast.makeText(this, "location:" + yourLocationLat + "," + yourLocationLon, Toast.LENGTH_SHORT).show()
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
            yourLocationLat = mLastLocation.latitude
            yourLocationLon = mLastLocation.longitude
            handler.sendEmptyMessage(0)
            // Toast.makeText(applicationContext, "location:" + yourLocationLat + "," + yourLocationLon, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    ////////////////////////////////////////////////////

    override fun onResume() {
        super.onResume()
        isUpdated = prefs.getBoolean("isUpdated", false)
        Log.i("ola", " " + isUpdated.toString() + "resume")
        if (isUpdated) {
            Log.i("lang", "onResumee")
            loc = prefs.getBoolean("USE_DEVICE_LOCATION", true)
            unit = prefs.getString("UNIT_SYSTEM", Setting.IMPERIAL.Value).toString()
            lang = prefs.getString("APP_LANG", Setting.ENGLISH.Value).toString()
            lon = prefs.getString("lon", ("")).toString()
            lat = prefs.getString("lat", ("")).toString()
            // setLocale(this, lang)
            if (loc) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            } else {
                viewModel.loadWeatherObj(
                    applicationContext,
                    lat.toDouble(),
                    lon.toDouble(),
                    lang,
                    unit
                )
            }
            Log.i("lang", "before res:" + lang + "," + unit + " " + loc + " " + lat + " " + lon)
            viewModel.updateAllData(applicationContext, lang, unit)
            editor.putBoolean("isUpdated", false)
            editor.commit()

        }

    }
fun checkWhiteListPermission(){
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
}
    override fun onPause() {
        super.onPause()
        editor.putBoolean("isUpdated", false)
        editor.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        welcomeScreen?.onSaveInstanceState(outState)
    }

}