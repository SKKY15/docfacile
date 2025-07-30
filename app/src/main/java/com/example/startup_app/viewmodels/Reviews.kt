package com.example.startup_app.viewmodels

data class Reviews(
    val patientName: String,
    val comment: String,
    val rating: Double,
    val date: String,
    val patientPhoto: Int,
    val doctorId: String,
)
