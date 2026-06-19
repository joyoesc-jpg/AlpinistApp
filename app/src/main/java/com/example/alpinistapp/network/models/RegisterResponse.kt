package com.example.alpinistapp.network.models

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserResponse?
)
