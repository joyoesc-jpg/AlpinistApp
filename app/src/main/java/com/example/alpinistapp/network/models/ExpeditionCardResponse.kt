package com.example.alpinistapp.network.models

data class ExpeditionCardResponse(
    val expedition_id: Int,

    val trail_id: Int,

    val trail_name: String,

    val date: String,

    val image: String
)