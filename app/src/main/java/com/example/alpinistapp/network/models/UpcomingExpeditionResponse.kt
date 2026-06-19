package com.example.alpinistapp.network.models

data class UpcomingExpeditionResponse(
    val expedition_id: Int,
    val date: String,

    val trail_id: Int,
    val name: String,

    val image: String
)

