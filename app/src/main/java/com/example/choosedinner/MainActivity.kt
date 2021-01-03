package com.example.choosedinner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 100
    private val myBundle = Bundle()

    private var myLocation: String? = null
    private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        }

        getLocation()
        setupButton()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "需要定位功能", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButton() {
        val myBtn = findViewById<Button>(R.id.get_location)
        myBtn.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)

            } else {
                if (myLocation != null) {
                    myBundle.putString("latlng", myLocation.toString())
                    showCardStackActivity()
                }
            }
        }

        val myBtnIn = findViewById<Button>(R.id.set_location)
        myBtnIn.setOnClickListener{
            val myEdittext = findViewById<EditText>(R.id.location)
            myBundle.putString("place", myEdittext.text.toString())
            Log.d("place", myEdittext.text.toString())
            showCardStackActivity()
        }
    }

    private fun showCardStackActivity() {
        val intent = Intent(this, CardStackActivity::class.java).apply {
            putExtras(myBundle)
        }
        startActivity(intent)
    }

    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            Log.d("getLocation", myLocation.toString())
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            myLocation = "${location.latitude}, ${location.longitude}"
            Log.d("location", myLocation.toString())
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}