package com.example.alpinistapp

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage // <-- IMPORTANTE: Importamos Coil para leer URLs de internet

@Composable
fun ExpeditionCard(
    expedition: Expedition,
    navController: NavController
) {
    // Encodificamos los strings para que no rompan la ruta de navegación de la MainActivity
    val encodedTitle = Uri.encode(expedition.title)
    val encodedDate = Uri.encode(expedition.date)
    val encodedUrl = Uri.encode(expedition.imageUrl) // Encodificamos el link de la imagen

    val encodedRoute = Uri.encode(expedition.route ?: expedition.title) // Fallback al título si viene nulo
    val encodedLocation = Uri.encode(expedition.location ?: "Ubicación")
    val encodedTrailImage = Uri.encode(expedition.image ?: expedition.imageUrl) // Fallback a la imagen de la exp
    val trailRating = (expedition.rating ?: 4.0).toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Espaciado entre tarjetas
            .clickable {
                navController.navigate(
                    "expedition_detail/$encodedTitle/$encodedDate/$encodedUrl?routeTitle=$encodedRoute&location=$encodedLocation&trailImage=$encodedTrailImage&rating=$trailRating"
                )
                       },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {

            AsyncImage(
                model = expedition.imageUrl, // El link que viene de Render/Neon
                contentDescription = "Imagen de la expedición",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            )

            Column(
                modifier = Modifier
                    .padding(end = 8.dp, start = 8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = expedition.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = expedition.date,
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}