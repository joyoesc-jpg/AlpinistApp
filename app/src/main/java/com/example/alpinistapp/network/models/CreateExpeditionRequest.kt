package com.example.alpinistapp.network.models


data class CreateExpeditionRequest(
    val trail_id: Int,
    val creator_id: Int,

    val date: String,

    val meeting_time: String? = null,

    val departure_time: String? = null
)

