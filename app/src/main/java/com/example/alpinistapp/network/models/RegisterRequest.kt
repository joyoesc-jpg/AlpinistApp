package com.example.alpinistapp.network.models

data class RegisterRequest(
    val name: String,
    val surname: String?,
    val email: String,
    val password: String,
    val phone_number: String?,
    val birthdate: String?
)
