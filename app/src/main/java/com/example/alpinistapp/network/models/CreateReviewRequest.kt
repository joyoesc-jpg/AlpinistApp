package com.example.alpinistapp.network.models

data class CreateReviewRequest(
    val registration_id: Int,
    val rating: Double,
    val description: String
)

