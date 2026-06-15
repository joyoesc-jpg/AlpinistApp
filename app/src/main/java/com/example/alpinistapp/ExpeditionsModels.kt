package com.example.alpinistapp

import com.google.gson.annotations.SerializedName

data class CreateExpeditionRequest(
    val title: String,
    val date: String,
    val time: String,
    @SerializedName("trail_id") val trailId: Int,
    @SerializedName("creator_id") val creatorId: Int
)

data class ExpeditionResponse(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    @SerializedName("trail_id") val trailId: Int,
    @SerializedName("creator_id") val creatorId: Int,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("member_count") val memberCount: Int = 0,
    @SerializedName("is_joined") val isJoined: Boolean = false
)

// Modelo para UNIRSE a una expedición
data class JoinExpeditionRequest(
    @SerializedName("user_id") val userId: Int // Cambiado
)

// Respuesta genérica de la API
data class ApiResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("expedition_id") val expeditionId: Int? = null
)