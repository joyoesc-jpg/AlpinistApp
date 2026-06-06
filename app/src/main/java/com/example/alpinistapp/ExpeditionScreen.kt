package com.example.alpinistapp

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ExpeditionScreen(
    title: String,
    date: String,
    imageUrl: String,
    routeTitle: String,   // Viene de 'route' en la BD
    location: String,     // Viene de 'location' en la BD
    trailImage: String,   // Viene de 'image' en la BD
    rating: Double,       // Viene de 'rating' en la BD (Double requerido por TrailScreen)
    navController: NavController
) {
    val listState = rememberLazyListState()
    val maxHeight = 260.dp
    val minHeight = 90.dp

    val collapseFraction = (listState.firstVisibleItemScrollOffset / 600f).coerceIn(0f, 1f)
    val animatedHeight by animateDpAsState(targetValue = lerp(maxHeight, minHeight, collapseFraction), label = "")

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF173963), Color(0xFF175294), Color(0xFF17635D))))
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Box {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Banner de la expedición $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedHeight)
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(text = title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = date, color = Color.White)
                    }

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(42.dp)
                            .background(Color(0xFFFFA726), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            }

            item {
                DetailContent(
                    routeTitle = routeTitle,
                    location = location,
                    trailImage = trailImage,
                    rating = rating,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    routeTitle: String,
    location: String,
    trailImage: String,
    rating: Double,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .offset(y = (-16).dp)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // ✅ REDIRECCIÓN AJUSTADA A TU MAINACTIVITY Y BASE DE DATOS REAL
        GradientButton("Información del sendero") {
            val encodedRoute = Uri.encode(routeTitle)
            val encodedLocation = Uri.encode(location)
            val encodedImage = Uri.encode(trailImage)
            val encodedDifficulty = Uri.encode("Media") // Fallback seguro para TrailScreen ya que mapeas ID en BD
            val trailRating = rating.toFloat()           // Ajustado al Float que espera tu ruta "detail/"

            // Dispara exactamente el destino de tu TrailScreen
            navController.navigate("detail/$encodedRoute/$encodedLocation/$encodedImage/$encodedDifficulty/$trailRating")
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton("Chat") {
            navController.navigate("chat_screen")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Personas:", color = Color.White)
        HorizontalDivider(color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        Column {
            participantsList.forEach { participant ->
                ParticipantCard(participant.name, participant.username)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        GradientButton("Salir de la expedición") {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}

data class Participant(val name: String, val username: String)
val participantsList = listOf(
    Participant("Alfonso Obregon", "AlfOber12"),
    Participant("Rogelio Juarez", "Roger"),
    Participant("Catalina Lopez", "Cathy")
)

@Composable
fun ParticipantCard(name: String, user: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).background(Color.LightGray, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name)
            Text(user, color = Color.Gray)
        }
    }
}