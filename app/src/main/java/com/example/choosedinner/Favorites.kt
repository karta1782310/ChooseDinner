package com.example.choosedinner

data class Favorites(
    val placeID: String,
    val name: String,
    val rating: String,
    val totalRatings: String,
    val photo: String
) {
    companion object {
        private var counter = 0L
    }
}