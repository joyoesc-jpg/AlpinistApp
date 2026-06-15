package com.example.alpinistapp

import android.net.Uri
import android.util.Log
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
import coil.compose.AsyncImage

@Composable
fun HomeTrailCard(
    trail: Trail,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                Log.d("HOME_NAV", "Navegando a detail con ID: ${trail.id}")

                val encodedRoute = Uri.encode(trail.routeTitle ?: "Sendero")
                val encodedLocation = Uri.encode(trail.location ?: "Ubicación desconocida")
                val encodedImage = Uri.encode(trail.imageUrl ?: "")
                val encodedDifficulty = Uri.encode(trail.difficulty ?: "Media")
                val trailRating = (trail.rating ?: 4.0).toFloat()

                navController.navigate("detail/${trail.id}/$encodedRoute/$encodedLocation/$encodedImage/$encodedDifficulty/$trailRating")
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
                model = trail.imageUrl,
                contentDescription = "Imagen del sendero",
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
                    text = trail.routeTitle ?: "Sin título",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${trail.location ?: "Ubicación"} • ${trail.difficulty ?: "Dificultad no especificada"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar rating si existe
                if (trail.rating != null) {
                    Text(
                        text = "⭐ ${trail.rating}/5",
                        fontSize = 14.sp,
                        color = Color(0xFFFFA726),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}