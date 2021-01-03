package com.example.choosedinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteListAdapter (private val context: Context,
                           private val dataSource: ArrayList<Favorites>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.item_favorite, parent, false)

        // Get element
        val nameTextView = rowView.findViewById(R.id.name) as TextView
        val rateTextView = rowView.findViewById(R.id.rate) as TextView
        val latTextView = rowView.findViewById(R.id.lat) as TextView
        val lngTextView = rowView.findViewById(R.id.lng) as TextView

        val recipe = getItem(position) as Favorites
        nameTextView.text = recipe.name
        rateTextView.text = recipe.rating
        latTextView.text = recipe.lat
        lngTextView.text = recipe.lng

        return rowView
    }
}