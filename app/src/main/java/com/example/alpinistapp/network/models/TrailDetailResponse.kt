package com.example.alpinistapp.network.models

data class TrailDetailResponse(
    val trail_id: Int,
    val name: String,
    val description: String,
    val route_gpx: String,

    val location: String,
    val difficulty: String,
    val route_type: String,

    val length: Double,
    val unevenness: Double,

    val cal_rating: Double,

    val max_recommended: Int,
    val max_people: Int,

    val image: String
)


