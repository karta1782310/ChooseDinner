package com.example.choosedinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestaurantCardStackAdapter(
    private var Restaurants: List<Restaurant> = emptyList()
) : RecyclerView.Adapter<RestaurantCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Restaurant = Restaurants[position]
        holder.name.text = "${Restaurant.id}. ${Restaurant.name}"
        holder.rating.text = "Rating:${Restaurant.rating}. Total:${Restaurant.totalRatings}"
        Glide.with(holder.image)
            .load(Restaurant.photo)
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, Restaurant.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return Restaurants.size
    }

    fun setRestaurants(Restaurants: List<Restaurant>) {
        this.Restaurants = Restaurants
    }

    fun getRestaurants(): List<Restaurant> {
        return Restaurants
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var rating: TextView = view.findViewById(R.id.item_rating)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}