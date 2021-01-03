package com.example.choosedinner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class FavoritesActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        listView = findViewById<ListView>(R.id.favorite_list)

        val myFavor = intent.extras?.getSerializable("Favorites") as ArrayList<Favorites>
        val adapter = FavoriteListAdapter(this, myFavor)
        listView.adapter = adapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as Favorites
                val urlx = "geo:" + selectedItem.lat + ", " + selectedItem.lng + "?q=" + selectedItem.name.split(" ", limit=2)[1]
                val intentOther = Intent(Intent.ACTION_VIEW, Uri.parse(urlx))
                startActivity(intentOther)
            }

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener{
            val rd = (0 until myFavor.size).random()
            val urlx = "geo:" + myFavor[rd].lat + ", " + myFavor[rd].lng + "?q=" + myFavor[rd].name.split(" ", limit=2)[1]
            val intentOther = Intent(Intent.ACTION_VIEW, Uri.parse(urlx))
            startActivity(intentOther)
        }
    }
}