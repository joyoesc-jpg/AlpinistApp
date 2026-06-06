package com.example.alpinistapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun TrailScreen(
    routeTitle: String,
    location: String,
    imageUrl: String,
    difficulty: String,
    rating: Double,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF173963),
                        Color(0xFF175294),
                        Color(0xFF17635D)
                    )
                )
            )
            .padding(16.dp)
    ) {

        // TÍTULO DINÁMICO
        Text(
            text = routeTitle,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SUBTÍTULO
        Text(
            text = "$location | $rating ★ | $difficulty",
            color = Color.White,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // IMAGEN DESDE LA NUBE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = routeTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Próximas expediciones:",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // TU LISTA CON EL 'it' DE TU CAPTURA
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(2) {
                ExpeditionInfoCard(
                    date = if (it == 0) "Miércoles 15 de abril" else "Jueves 18 de junio",
                    creator = if (it == 0) "Cathy" else "Roger",
                    joined = it == 0
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // BOTONES
        GradientButton(text = "Crear expedición", onClick = {})
        Spacer(modifier = Modifier.height(12.dp))
        GradientButton(text = "Descargar mapa", onClick = {})
    }
}

@Composable
fun ExpeditionInfoCard(
    date: String,
    creator: String,
    joined: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(
                width = if (joined) 2.dp else 0.dp,
                color = if (joined) Color.White else Color.Transparent,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Creado por $creator",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = if (joined) "Unirme" else "Info",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}