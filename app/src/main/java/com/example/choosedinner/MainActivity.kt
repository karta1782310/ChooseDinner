package com.example.choosedinner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 100
    private val myBundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButton()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showCardStackActivity()
            } else {
                Toast.makeText(this, "需要定位功能", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val locationButtonHandler = View.OnClickListener { view ->
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        } else {
            showCardStackActivity()
        }
    }

    private fun setupButton() {
        val myBtn = findViewById<Button>(R.id.get_location)
        myBtn.setOnClickListener(locationButtonHandler)

        val myBtnIn = findViewById<Button>(R.id.set_location)
        myBtnIn.setOnClickListener{
            val myEdittext = findViewById<EditText>(R.id.location)
            myBundle.putString("place", myEdittext.text.toString())
            showCardStackActivity()
        }
    }

    private fun showCardStackActivity() {
        val intent = Intent(this, CardStackActivity::class.java).apply {
            putExtra("bundle", myBundle)
        }
        startActivity(intent)
    }
}