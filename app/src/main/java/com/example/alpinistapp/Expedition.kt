package com.example.alpinistapp

data class Expedition(
    val id: Int,
    val title: String = "",
    val date: String,
    val imageUrl: String = "",
    val trailId: Int? = null,
    val route: String? = null,
    val location: String? = null,
    val rating: Double? = null,
    val image: String? = null,
    val creator: String,
    val joined: Boolean
)