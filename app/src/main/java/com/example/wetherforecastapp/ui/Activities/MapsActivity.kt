package com.example.wetherforecastapp.ui.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.wetherforecastapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.net.MalformedURLException
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var confirmBtn: Button
    private lateinit var layersSpinner: Spinner
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var prefs: SharedPreferences
    private lateinit var tileOverlayTransparent: TileOverlay
    private  var tileLayer="clouds"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        confirmBtn = findViewById(R.id.confirmBtn)
        layersSpinner= findViewById(R.id.tileType)

        confirmBtn.setOnClickListener {
            if (lat == 0.0 || lon == 0.0) {
                Toast.makeText(this, "please select place", Toast.LENGTH_SHORT).show()
            } else {
                if (intent.hasExtra("mapId")) {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    intent.putExtra("lat", lat)
                    intent.putExtra("lon", lon)
                    intent.putExtra("id", 1)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, SettingsActivity::class.java)
                    prefs = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor: SharedPreferences.Editor = prefs.edit()
                    editor.putString("lat", (lat.toString()))
                    editor.putString("lon", (lon.toString()))
                    editor.apply()
                    editor.commit()
                    startActivity(intent)
                    finish()
                }
            }
        }
        if(intent.hasExtra("layer")) {
            confirmBtn.visibility = View.GONE
            layersSpinner.visibility = View.VISIBLE
        }
        else{
            confirmBtn.visibility = View.VISIBLE
            layersSpinner.visibility = View.GONE
        }
        layersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tileLayer = parent?.getItemAtPosition(position).toString()
                if (!(tileLayer.isNullOrEmpty())) {
                    tileOverlayTransparent.remove()
                    onMapReady(mMap)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { point ->
            lat = point.latitude
            lon = point.longitude
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(point))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
        }
        if(intent.hasExtra("layer")){
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                @Synchronized
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                    val url ="https://tile.openweathermap.org/map/$tileLayer"+"_new/$zoom/$x/$y.png?appid=31cceaa80d19afe5ea2ec0f5b270311b"
                    return  try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw AssertionError(e)
                    }
                }

            }
            tileOverlayTransparent = mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(!(intent.hasExtra("mapId"))) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("USE_DEVICE_LOCATION", true)
            editor.apply()
            editor.commit()
        }

    }
}