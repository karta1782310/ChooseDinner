package com.example.choosedinner

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.navigation.NavigationView
import com.yuyakaido.android.cardstackview.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Thread.sleep
import java.util.*

class CardStackActivity : AppCompatActivity(), CardStackListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createRestaurants()) }

    private val myIntent by lazy { Intent(this, FavoritesActivity::class.java) }
    private val myBundle = Bundle()
    private val myFavor = ArrayList<Favorites>()

    private var myJson: String? = null
    private var myLocation: String? = null
    private var locationManager : LocationManager? = null
    private val demoPhotos: List<String> = listOf(
        "https://source.unsplash.com/Xq1ntWruZQI/600x800",
        "https://source.unsplash.com/NYyCqdBOKwc/600x800",
        "https://source.unsplash.com/buF62ewDLcQ/600x800",
        "https://source.unsplash.com/THozNzxEP3g/600x800",
        "https://source.unsplash.com/PeFk7fzxTdk/600x800",
        "https://source.unsplash.com/LrMWHKqilUw/600x800",
        "https://source.unsplash.com/HN-5Z6AmxrM/600x800",
        "https://source.unsplash.com/CdVAUADdqEc/600x800",
        "https://source.unsplash.com/AWh9C-QjhE4/600x800"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread(BackgroundFetcher()).start()

        getLocation()
        setContentView(R.layout.activity_cardstack)
        setupNavigation()
        setupCardStackView()
        setupButton()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        var tmp = Favorites(
            name =  view.findViewById<TextView>(R.id.item_name).text.toString(),
            rating = view.findViewById<TextView>(R.id.item_rating).text.toString(),
            lat =  view.findViewById<TextView>(R.id.lat).text.toString(),
            lng =  view.findViewById<TextView>(R.id.lng).text.toString()
        )
        myFavor.add(tmp)
        Log.d("myFavorList", "${myFavor.size}")
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupNavigation() {
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.reload -> reload()
                R.id.favorite -> gotoFavorite()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupCardStackView() {
//        if (Build.VERSION.SDK_INT > 9) {
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
//        }

        initialize()
    }

    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun paginate() {
        val old = adapter.getRestaurants()
        val new = old.plus(createRestaurants())
        val callback = RestaurantDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRestaurants(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getRestaurants()
        val new = createRestaurants()
        val callback = RestaurantDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRestaurants(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun gotoFavorite() {
        myBundle.putSerializable("Favorites", myFavor)
//        myIntent.putExtra("Like", myBundle);
        myIntent.putExtras(myBundle)
        startActivity(myIntent)
    }

//    private fun addFirst(size: Int) {
//        val old = adapter.getSpots()
//        val new = mutableListOf<Spot>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                add(manager.topPosition, createSpot())
//            }
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setSpots(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun addLast(size: Int) {
//        val old = adapter.getSpots()
//        val new = mutableListOf<Spot>().apply {
//            addAll(old)
//            addAll(List(size) { createSpot() })
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setSpots(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun removeFirst(size: Int) {
//        if (adapter.getSpots().isEmpty()) {
//            return
//        }
//
//        val old = adapter.getSpots()
//        val new = mutableListOf<Spot>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                removeAt(manager.topPosition)
//            }
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setSpots(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//
//    private fun removeLast(size: Int) {
//        if (adapter.getSpots().isEmpty()) {
//            return
//        }
//
//        val old = adapter.getSpots()
//        val new = mutableListOf<Spot>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                removeAt(this.size - 1)
//            }
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setSpots(new)
//        result.dispatchUpdatesTo(adapter)
//    }
//


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            myLocation = "${location.latitude},${location.longitude}"
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
            Log.d("getLocation", myLocation.toString())
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private fun createRestaurants(): List<Restaurant> {
//        val apiResponse = URL("https://github.com/karta1782310/ChooseDinner/blob/master/app/src/main/assets/NearBySearch.json").readText()

        while (myJson == null) {
            sleep(1000)
            Log.d("sleep", "Waiting for json.")
        }

        val restaurants = ArrayList<Restaurant>()

        val objMySearch = JSONObject(myJson)
        val arrRestaurants: JSONArray = objMySearch.getJSONArray("results")
        for (i in 0 until arrRestaurants.length()) {
            val objRestaurant: JSONObject = arrRestaurants.getJSONObject(i)
            val placeID: String = objRestaurant.getString("place_id")
            val name: String = objRestaurant.getString("name")

            val objGeometry = objRestaurant.getJSONObject("geometry")
            val objLoc = objGeometry.getJSONObject("location")
            val lat = objLoc.getString("lat")
            val lng = objLoc.getString("lng")

            var rating = "null"
            var totalRatings = "null"
            var photo_url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"

            try {
                rating = objRestaurant.getString("rating")
            } catch (e: JSONException) {
                Log.d("Json", e.toString())
            }
            try {
                totalRatings = objRestaurant.getString("user_ratings_total")
            } catch (e: JSONException) {
                Log.d("Json", e.toString())
            }
            try {
                val photos: JSONArray = objRestaurant.getJSONArray("photos")
                val photo: String = photos.getJSONObject(0).getString("photo_reference")
                photo_url =
                    "https://maps.googleapis.com/maps/api/place/photo?key=${BuildConfig.API_KEY}&photoreference=${photo}&maxheight=800&maxwidth=600"
            } catch (e: JSONException) {
                Log.d("Json", e.toString())
            }

//            try {
//                val openingHours: JSONObject = objRestaurant.getJSONObject("opening_hours")
//                val opening: Boolean = openingHours.getBoolean("open_now")
//                if (!opening) continue
//            } catch (e: JSONException) {
//                Log.d("JSONobject", "Not get opening hours.")
//            }

            restaurants.add(
                Restaurant(
                    placeID = placeID,
                    name = name,
                    rating = rating,
                    totalRatings = totalRatings,
                    photo = photo_url, //demoPhotos[i % 9]
                    lat = lat,
                    lng = lng
                )
            )
            Log.d("GoogleApi", restaurants[restaurants.size - 1].name)
        }
        return restaurants
    }

    private fun getJson(url: String) : String?  {  // (2)
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body?.string()
    }

    inner class BackgroundFetcher : Runnable {
        override fun run() {  // (3)
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=22.6278362,%20120.2630863&radius=1500&type=restaurant&key=${BuildConfig.API_KEY}"
            myJson = getJson(url)
        }
    }


}