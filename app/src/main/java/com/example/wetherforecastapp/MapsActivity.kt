package com.example.wetherforecastapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var confirmBtn: Button
    private  var lat: Double=0.0
    private  var lang: Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        confirmBtn=findViewById(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            if (lat==0.0 || lang==0.0) {
                Toast.makeText(
                    this,
                    "please select place , ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                     ", " + lang+lat,
                    Toast.LENGTH_SHORT
                ).show()
                val intent: Intent = Intent(this,FavoriteActivity::class.java)
                intent.putExtra("lat",lat)
                intent.putExtra("lon",lang)
                intent.putExtra("id",1)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { point ->
          /*  Toast.makeText(
                this,
                point.latitude.toString() + ", " + point.longitude,
                Toast.LENGTH_SHORT
            ).show()*/
            lat=point.latitude.toDouble()
            lang=point.longitude.toDouble()
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(point))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
        }
    }
}