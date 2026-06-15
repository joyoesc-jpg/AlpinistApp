package com.example.alpinistapp

import com.google.gson.annotations.SerializedName

data class Expedition(
    val id: Int,
    val title: String = "",
    val date: String,
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("trail_id") val trailId: Int?,
    val route: String? = null,
    val location: String? = null,
    val rating: Double? = null,
    val image: String? = null,
    val creator: String,
    val joined: Boolean
)