package com.example.alpinistapp.network.models

data class TrailCardResponse(
    val trail_id: Int,
    val name: String,
    val location: String,
    val difficulty: String,
    val cal_rating: Double,
    val image: String
)


