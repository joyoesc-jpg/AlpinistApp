package com.example.alpinistapp.network.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserResponse?
)

data class UserResponse(
    val user_id: Int,
    val name: String,
    val email: String
)
