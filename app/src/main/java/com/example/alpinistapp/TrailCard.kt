package com.example.alpinistapp

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

@Composable
fun TrailCard(
    trail: Trail,
    navController: NavController
){
    // Encodificamos las variables con sus nuevos nombres del modelo
    val encodedRoute = Uri.encode(trail.routeTitle)
    val encodedLocation = Uri.encode(trail.location)
    val encodedImage = Uri.encode(trail.imageUrl)
    val encodedDifficulty = Uri.encode(trail.difficulty)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // La ruta debe coincidir exactamente con el orden de MainActivity
                navController.navigate("detail/$encodedRoute/$encodedLocation/$encodedImage/$encodedDifficulty/${trail.rating}")
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        Column{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = trail.routeTitle, // Corregido
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = trail.location,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            AsyncImage(
                model = trail.imageUrl, // Corregido
                contentDescription = "Imagen de la ruta ${trail.routeTitle}", // Corregido
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrailCardPreview() {
    // Creamos un objeto "falso" con datos de prueba
    val mockTrail = Trail(
        routeTitle = "Nevado de Toluca",
        location = "Estado de México",
        difficulty = "Media",
        imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b", // URL de prueba
        rating = 4.8
    )

    // Dibujamos la tarjeta pasándole un NavController simulado
    TrailCard(
        trail = mockTrail,
        navController = rememberNavController()
    )
}