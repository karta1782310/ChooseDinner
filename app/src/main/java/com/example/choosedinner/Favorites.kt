package com.example.choosedinner

import java.io.Serializable

data class Favorites (
    val name: String,
    val rating: String,
    val lat: String,
    val lng: String
) : Serializable