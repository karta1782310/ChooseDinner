package com.example.choosedinner

data class Restaurant(
    val id: Long = counter++,
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