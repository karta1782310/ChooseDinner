package com.example.choosedinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CardStackAdapter(
    private var restaurants: List<Restaurant> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.name.text = "${restaurant.id}. ${restaurant.name}"
        holder.rating.text = "Rating:${restaurant.rating}. Total:${restaurant.totalRatings}"
        Glide.with(holder.image)
            .load(restaurant.photo)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, restaurant.name, Toast.LENGTH_SHORT).show()
        }

        holder.lat.text = "${restaurant.lat}"
        holder.lng.text = "${restaurant.lng}"
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun setRestaurants(restaurants: List<Restaurant>) {
        this.restaurants = restaurants
    }

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var rating: TextView = view.findViewById(R.id.item_rating)
        var image: ImageView = view.findViewById(R.id.item_image)
        var lat: TextView = view.findViewById(R.id.lat)
        var lng: TextView = view.findViewById(R.id.lng)
    }

}