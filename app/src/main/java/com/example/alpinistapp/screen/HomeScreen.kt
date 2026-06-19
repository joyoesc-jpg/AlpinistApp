package com.example.alpinistapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alpinistapp.SessionManager
import com.example.alpinistapp.components.ExpeditionCard
import com.example.alpinistapp.network.RetrofitClient
import com.example.alpinistapp.network.models.ExpeditionCardResponse
import com.example.alpinistapp.network.models.UpcomingExpeditionResponse

@Composable
fun HomeScreen(
    navController: NavController
){
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId by sessionManager.userId.collectAsState(initial = null)
    val userName by sessionManager.userName.collectAsState(initial = "")

    var upcomingExpeditions by remember { mutableStateOf<List<UpcomingExpeditionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        userId?.let { id ->
            try {
                upcomingExpeditions = RetrofitClient.api.getUpcomingExpeditions(id)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2))
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF173963),
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    )
                ),
            contentAlignment = Alignment.BottomStart
        ){
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Hola,",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = userName ?: "Montañero",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Tus próximas expediciones...",
            modifier = Modifier.padding(start = 24.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF173963)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF173963))
            }
        } else if (upcomingExpeditions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No tienes expediciones programadas",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn (
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(upcomingExpeditions) { expedition ->
                    ExpeditionCard(
                        expedition = ExpeditionCardResponse(
                            expedition_id = expedition.expedition_id,
                            trail_id = expedition.trail_id,
                            trail_name = expedition.name,
                            date = expedition.date,
                            image = expedition.image
                        ),
                        navController = navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
