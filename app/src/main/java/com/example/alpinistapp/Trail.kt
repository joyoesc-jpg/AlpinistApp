package com.example.alpinistapp

data class Trail(
    val id: Int,
    val routeTitle: String,
    val location: String,
    val imageUrl: String,
    val difficulty: String,
    val rating: Double,
    val type: String = "Senderismo"
)