package com.example.alpinistapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alpinistapp.SessionManager
import com.example.alpinistapp.network.RetrofitClient
import com.example.alpinistapp.components.GradientButton
import com.example.alpinistapp.components.MapPreview
import com.example.alpinistapp.components.CreateExpeditionDialog
import com.example.alpinistapp.network.models.TrailDetailResponse
import kotlinx.coroutines.launch

@Composable
fun TrailScreen(
    trailId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId by sessionManager.userId.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var trail by remember { mutableStateOf<TrailDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(trailId) {
        try {
            trail = RetrofitClient.api.getTrail(trailId)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF173963)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (trail == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF173963)),
            contentAlignment = Alignment.Center
        ) {
            Text("Error al cargar el sendero", color = Color.White)
        }
    } else {
        val currentTrail = trail!!
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF173963), Color(0xFF175294), Color(0xFF17635D))
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = currentTrail.image,
                            contentDescription = currentTrail.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = currentTrail.name,
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentTrail.location} | ${currentTrail.cal_rating} ★",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentTrail.route_gpx.isNotBlank()) {
                            MapPreview(
                                gpxContent = currentTrail.route_gpx,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Mapa no disponible", color = Color.White)
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información del sendero",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        listOf(
                            "📏 Distancia" to "${currentTrail.length} km",
                            "⛰ Desnivel" to "${currentTrail.unevenness} m",
                            "📊 Dificultad" to currentTrail.difficulty,
                            "🔁 Tipo" to currentTrail.route_type,
                            "👥 Grupo recomendado" to "Hasta ${currentTrail.max_recommended}"
                        ).forEach { (label, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, fontWeight = FontWeight.Medium, color = Color.Black)
                                Text(text = value, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Descripción",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = currentTrail.description,
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Próximas expediciones",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        GradientButton(
                            text = "Crear expedición",
                            onClick = { showCreateDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GradientButton(
                            text = "Ver todas las expediciones",
                            onClick = { /* TODO */ }
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reseñas",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { /* TODO */ }) {
                                Text(text = "Añadir reseña", color = Color(0xffff9b3d))
                              }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }

            if (showCreateDialog) {
                userId?.let { uid ->
                    CreateExpeditionDialog(
                        trailId = trailId,
                        creatorId = uid,
                        onDismiss = { showCreateDialog = false },
                        onCreate = { request ->
                            scope.launch {
                                try {
                                    RetrofitClient.api.createExpedition(request)
                                    showCreateDialog = false
                                    // Refresh or Navigate? 
                                    // For now, just close.
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                } ?: run {
                    // Optional: Show a message that user needs to be logged in
                    showCreateDialog = false
                }
            }
        }
    }
}
