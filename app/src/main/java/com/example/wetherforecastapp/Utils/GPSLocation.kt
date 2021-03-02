package com.example.wetherforecastapp.Utils


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.Context
import android.location.Location
import android.provider.Settings

class GPSLocation {
    private val REQUEST_LOCATION = 1
    private var locationManager: LocationManager? = null
    private var latitude: String? = null
    private  var longitude:String? = null


    fun initPermissions(activity: Activity?) {
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
        )
    }

    fun findDeviceLocation(activity: Activity) {
        locationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Check gps is enable or not
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            gpsEnable(activity)
        } else {
            //GPS is already On then
            getLocation(activity)
        }
    }

    private fun getLocation(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                        activity,
                       ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity,
                       ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity,arrayOf(ACCESS_FINE_LOCATION),REQUEST_LOCATION)
        } else {
            val LocationGps: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val LocationNetwork: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val LocationPassive: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (LocationGps != null) {
                val lat: Double = LocationGps.getLatitude()
                val longi: Double = LocationGps.getLongitude()
                latitude = lat.toString()
                longitude = longi.toString()
            } else if (LocationNetwork != null) {
                val lat: Double = LocationNetwork.getLatitude()
                val longi: Double = LocationNetwork.getLongitude()
                latitude = lat.toString()
                longitude = longi.toString()
            } else if (LocationPassive != null) {
                val lat: Double = LocationPassive.getLatitude()
                val longi: Double = LocationPassive.getLongitude()
                latitude = lat.toString()
                longitude = longi.toString()
            } else {
                Toast.makeText(activity, "Can't Get Your Location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gpsEnable(activity: Activity) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("YES",
                        DialogInterface.OnClickListener { dialog, which ->
                            activity.startActivity(
                                    Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                    )
                            )
                        }).setNegativeButton("NO",
                        DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }

    fun getLatitude(): String? {
        return latitude
    }

    fun getLongitude(): String? {
        return longitude
    }

}