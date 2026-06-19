package com.example.alpinistapp

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

enum class SearchType {
    Trails, Expeditions
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("Todas") }
    var searchType by remember { mutableStateOf(SearchType.Trails) }

    var allTrails by remember { mutableStateOf<List<Trail>>(emptyList()) }
    var allExpeditions by remember { mutableStateOf<List<Expedition>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val difficulties = listOf("Todas", "Baja", "Media", "Alta")

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            // Fetch both in parallel or sequence
            allTrails = RetrofitClient.apiService.getTrails()
            allExpeditions = RetrofitClient.apiService.getExpeditions()
        } catch (e: Exception) {
            errorMessage = "No se pudieron recuperar los datos de la montaña."
        } finally {
            isLoading = false
        }
    }

    val filteredTrails = allTrails.filter { trail ->
        val title = trail.routeTitle ?: ""
        val location = trail.location ?: ""
        val difficulty = trail.difficulty ?: ""

        val matchesQuery = title.contains(searchQuery, ignoreCase = true) ||
                location.contains(searchQuery, ignoreCase = true)

        val matchesDifficulty = selectedDifficulty == "Todas" ||
                difficulty.equals(selectedDifficulty, ignoreCase = true)

        matchesQuery && matchesDifficulty
    }

    val filteredExpeditions = allExpeditions.filter { expedition ->
        val title = expedition.title ?: ""
        val date = expedition.date ?: ""
        
        title.contains(searchQuery, ignoreCase = true) ||
                date.contains(searchQuery, ignoreCase = true)
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
                
                // Selector de tipo de búsqueda
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { searchType = SearchType.Trails },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (searchType == SearchType.Trails) Color(0xffff9b3d) else Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Senderos")
                    }
                    Button(
                        onClick = { searchType = SearchType.Expeditions },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (searchType == SearchType.Expeditions) Color(0xffff9b3d) else Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    placeholder = { 
                        Text(if (searchType == SearchType.Trails) "¿A dónde quieres ir?" else "Nombre o fecha de expedición") 
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                if (searchType == SearchType.Trails) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Difficulty Filter (Solo para senderos)
                    Text(
                        text = "Dificultad",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(difficulties) { difficulty ->
                            val isSelected = selectedDifficulty == difficulty
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDifficulty = difficulty },
                                label = { Text(difficulty) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    labelColor = Color.White,
                                    selectedContainerColor = Color(0xffff9b3d),
                                    selectedLabelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Color.White.copy(alpha = 0.3f),
                                    selectedBorderColor = Color.Transparent,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 0.dp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF175294))
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        } else {
            val count = if (searchType == SearchType.Trails) filteredTrails.size else filteredExpeditions.size
            Text(
                text = "Resultados ($count)",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            if ((searchType == SearchType.Trails && filteredTrails.isEmpty()) || 
                (searchType == SearchType.Expeditions && filteredExpeditions.isEmpty())) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron resultados con esos criterios.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (searchType == SearchType.Trails) {
                        items(filteredTrails) { trail ->
                            TrailCard(
                                trail = trail,
                                navController = navController
                            )
                        }
                    } else {
                        items(filteredExpeditions) { expedition ->
                            ExpeditionCard(
                                expedition = expedition,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController = navController)
}
