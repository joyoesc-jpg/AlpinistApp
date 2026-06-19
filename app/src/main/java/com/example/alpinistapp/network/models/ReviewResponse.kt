package com.example.alpinistapp.network.models

data class ReviewResponse(
    val review_id: Int,

    val rating: Double,

    val description: String,

    val date: String,

    val name: String,

    val surname: String?
)