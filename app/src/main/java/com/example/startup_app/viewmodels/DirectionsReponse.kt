package com.example.startup_app.viewmodels

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline
)

data class Leg(
    val steps: List<Step>,
    val distance: Distance,
    val duration: Duration,
    @SerializedName("duration_in_traffic") val durationInTraffic: Duration?
)

data class Step(
    val polyline: Polyline,
    val duration: Duration,
    @SerializedName("duration_in_traffic") val durationInTraffic: Duration?,
    @SerializedName("html_instructions") val htmlInstructions: String?
)

data class Polyline(
    val points: String
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class OverviewPolyline(
    val points: String
)
