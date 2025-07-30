package com.example.startup_app.viewmodels

import java.util.UUID

data class Booking(
    val id: String = UUID.randomUUID().toString(),
    var date: String,
    var time : String,
    val doctorId: String,
    var status: String = "upcoming",
    var rating: Int? = null,
    var isNotified: Boolean = false,
)