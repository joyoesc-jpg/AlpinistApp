package com.example.alpinistapp

import android.net.Uri
import android.util.Log
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun TrailCard(
    trail: Trail,
    navController: NavController
){
    val encodedRoute = Uri.encode(trail.routeTitle)
    val encodedLocation = Uri.encode(trail.location)
    val encodedImage = Uri.encode(trail.imageUrl)
    val encodedDifficulty = Uri.encode(trail.difficulty)
    Log.d("DEBUG_RUTA", "Sendero: ${trail.routeTitle}, ID: ${trail.id}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (trail.id != 0) {
                    navController.navigate("detail/${trail.id}/$encodedRoute/$encodedLocation/$encodedImage/$encodedDifficulty/${trail.rating}")
                } else {
                    Log.e("NAV_ERROR", "Intentaste navegar con un ID de ruta 0")
                }            },
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
                    text = trail.routeTitle,
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
                model = trail.imageUrl,
                contentDescription = "Imagen de la ruta ${trail.routeTitle}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        }
    }
}