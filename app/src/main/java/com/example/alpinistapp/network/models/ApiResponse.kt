package com.example.alpinistapp.network.models

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val expedition_id: Int? = null
)
