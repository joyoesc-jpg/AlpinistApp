package com.example.alpinistapp.network.models

data class ExpeditionDetailResponse(
    val expedition_id: Int,
    val trail_id: Int,
    val trail_name: String,
    val date: String,

    val meeting_time: String?,
    val departure_time: String?,
    val arrival_time: String?,
    val end_time: String?
)