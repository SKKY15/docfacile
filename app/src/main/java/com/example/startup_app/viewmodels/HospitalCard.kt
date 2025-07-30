package com.example.startup_app.viewmodels

data class HospitalCard(
    val id : Int,
    val name : String,
    val image : Int,
    val rating : Double,
    val reviews : Int,
    val street : String,
    val time : String,
    val mapsUrl: String,
    val latitude: Double,
    val longitude: Double,
    var isFavorite : Boolean = false,
)