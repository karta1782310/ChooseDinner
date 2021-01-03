package com.example.choosedinner

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable
import java.util.ArrayList

class FavoritesActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        listView = findViewById<ListView>(R.id.favorite_list)

        val myFavor = intent.extras?.getSerializable("Favorites") as ArrayList<Favorites>
        val adapter = FavoriteListAdapter(this, myFavor)
        listView.adapter = adapter
    }
}