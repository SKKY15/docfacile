package com.example.startup_app.viewmodels


enum class WeekDay {
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday;



}


data class WorkingTimeModel(
    val startDay: WeekDay,
    val endDay: WeekDay,
    val startTime: String,
    val endTime: String
) {
    override fun toString(): String {
        return "$startDay - $endDay / $startTime - $endTime"
    }
}
data class Doctor(
    val name: String,
    val image: Int,
    val id : Int,
    var isFavorite: Boolean = false,
    val speciality: String,
    var street: String,
    val rating: Double,
    var numPatients: Int,
    var experienceYear: Int,
    var bio: String = "",
    val reviews: Int,
    val workingTime: WorkingTimeModel,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
