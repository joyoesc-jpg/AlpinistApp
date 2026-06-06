package com.example.alpinistapp

data class Expedition(
    val id: Int,
    val title: String,         // El nombre de la expedición
    val date: String,          // La fecha de la expedición
    val imageUrl: String,     // Columna TEXT 'image_url' de tu tabla expedition
    val trailId: Int?,        // ID del sendero asociado (Nullable por si acaso)

    // 👇 Campos del JOIN con la tabla 'trail' indispensables para redirigir
    val route: String?,        // Columna VARCHAR(100) 'route' de tu tabla trail
    val location: String?,     // Columna VARCHAR(100) 'location' de tu tabla trail
    val rating: Double?,       // Columna NUMERIC(3,2) 'rating' de tu tabla trail
    val image: String?         // Columna TEXT 'image' (imagen específica del sendero si difiere)
)