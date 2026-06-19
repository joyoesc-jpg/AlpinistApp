package com.example.alpinistapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alpinistapp.network.RetrofitClient
import com.example.alpinistapp.components.ExpeditionCard
import com.example.alpinistapp.components.TrailCard
import com.example.alpinistapp.network.models.ExpeditionCardResponse
import com.example.alpinistapp.network.models.TrailCardResponse

enum class SearchType { Trails, Expeditions }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("Todas") }
    var searchType by remember { mutableStateOf(SearchType.Trails) }

    // Data from Network
    var allTrails by remember { mutableStateOf(listOf<TrailCardResponse>()) }
    var allExpeditions by remember { mutableStateOf(listOf<ExpeditionCardResponse>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            allTrails = RetrofitClient.api.getTrails()
            allExpeditions = RetrofitClient.api.getExpeditions()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val difficulties = listOf("Todas", "Baja", "Media", "Alta")

    val filteredTrails = allTrails.filter { trail ->
        val matchesQuery = trail.name.contains(searchQuery, ignoreCase = true) ||
                trail.location.contains(searchQuery, ignoreCase = true)
        val matchesDifficulty = selectedDifficulty == "Todas" ||
                trail.difficulty.equals(selectedDifficulty, ignoreCase = true)
        matchesQuery && matchesDifficulty
    }

    val filteredExpeditions = allExpeditions.filter { expedition ->
        expedition.trail_name.contains(searchQuery, ignoreCase = true) ||
                expedition.date.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF173963),
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    )
                )
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Text(
                    text = if (searchType == SearchType.Trails) "Buscar Senderos" else "Buscar Expediciones",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { searchType = SearchType.Trails },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (searchType == SearchType.Trails) Color(0xffff9b3d) else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Senderos")
                    }
                    Button(
                        onClick = { searchType = SearchType.Expeditions },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (searchType == SearchType.Expeditions) Color(0xffff9b3d) else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Expediciones")
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)),
                    placeholder = {
                        Text(if (searchType == SearchType.Trails) "¿A dónde quieres ir?" else "Nombre o fecha")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (searchType == SearchType.Trails) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(difficulties) { difficulty ->
                            FilterChip(
                                selected = selectedDifficulty == difficulty,
                                onClick = { selectedDifficulty = difficulty },
                                label = { Text(difficulty) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF173963))
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (searchType == SearchType.Trails) {
                    items(filteredTrails) { trail ->
                        TrailCard(trail = trail, navController = navController)
                    }
                } else {
                    items(filteredExpeditions) { expedition ->
                        ExpeditionCard(expedition = expedition, navController = navController)
                    }
                }
            }
        }
    }
}
