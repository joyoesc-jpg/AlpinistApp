package com.example.alpinistapp.screen

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alpinistapp.SessionManager
import com.example.alpinistapp.network.RetrofitClient
import com.example.alpinistapp.network.models.JoinExpeditionRequest
import com.example.alpinistapp.network.models.ParticipantResponse
import com.example.alpinistapp.network.models.TrailDetailResponse
import kotlinx.coroutines.launch

@Composable
fun ExpeditionScreen(
    expeditionId: Int,
    trailId: Int,
    date: String,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val maxHeight = 260.dp
    val minHeight = 90.dp

    val collapseFraction = (listState.firstVisibleItemScrollOffset / 600f).coerceIn(0f, 1f)
    val animatedHeight by animateDpAsState(targetValue = if (collapseFraction > 0.5f) minHeight else maxHeight, label = "")

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId by sessionManager.userId.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var trail by remember { mutableStateOf<TrailDetailResponse?>(null) }
    var participants by remember { mutableStateOf<List<ParticipantResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun refreshData() {
        scope.launch {
            try {
                trail = RetrofitClient.api.getTrail(trailId)
                participants = RetrofitClient.api.getParticipants(expeditionId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(expeditionId, trailId) {
        refreshData()
    }

    val isUserInExpedition = participants.any { it.user_id == userId }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF173963)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (trail == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF173963)), contentAlignment = Alignment.Center) {
            Text("Error al cargar datos", color = Color.White)
        }
    } else {
        val currentTrail = trail!!
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
                            model = currentTrail.image,
                            contentDescription = "Banner",
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
                            Text(text = currentTrail.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                        routeTitle = currentTrail.name,
                        location = currentTrail.location,
                        trailImage = currentTrail.image,
                        rating = currentTrail.cal_rating,
                        trailId = trailId,
                        expeditionId = expeditionId,
                        userId = userId,
                        isUserInExpedition = isUserInExpedition,
                        participants = participants,
                        navController = navController,
                        onRefresh = { refreshData() }
                    )
                }
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
    trailId: Int,
    expeditionId: Int,
    userId: Int?,
    isUserInExpedition: Boolean,
    participants: List<ParticipantResponse>,
    navController: NavController,
    onRefresh: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .offset(y = (-16).dp)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate("trail_detail/$trailId")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffff9b3d))
        ) {
            Text("Información del sendero")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Participantes:", color = Color.White, fontWeight = FontWeight.Bold)
        HorizontalDivider(color = Color.White.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (participants.isEmpty()) {
                Text("No hay participantes aún", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            } else {
                participants.forEach { participant ->
                    ParticipantCard(participant.name, "${participant.surname ?: ""}")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        if (isUserInExpedition) {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            userId?.let {
                                RetrofitClient.api.leaveExpedition(expeditionId, it)
                                onRefresh()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f))
            ) {
                Text("Salir de la expedición")
            }
        } else {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            userId?.let {
                                RetrofitClient.api.joinExpedition(expeditionId, JoinExpeditionRequest(it))
                                onRefresh()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Unirse a la expedición")
            }
        }
    }
}

@Composable
fun ParticipantCard(name: String, surname: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF175294).copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF175294)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = "$name $surname", fontWeight = FontWeight.Medium, color = Color.Black)
            Text(text = "Participante", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
