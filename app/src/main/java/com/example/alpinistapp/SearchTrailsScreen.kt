package com.example.alpinistapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SearchTrailsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Mock data using available resources
    val allTrails = listOf(
        Trail("Cumbres del Ajusco", "CDMX, México", R.drawable.ajusco),
        Trail("Popocatépetl", "Puebla, México", R.drawable.popocatepetl),
        Trail("Iztaccíhuatl", "Estado de México, México", R.drawable.ajusco),
        Trail("Nevado de Toluca", "Toluca, México", R.drawable.popocatepetl),
        Trail("Pico de Orizaba", "Veracruz, México", R.drawable.ajusco),
        Trail("La Malinche", "Tlaxcala, México", R.drawable.popocatepetl)
    )

    val filteredTrails = if (searchQuery.isEmpty()) {
        allTrails
    } else {
        allTrails.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2))

    ) {
        // Header
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
                    text = "Buscar Senderos",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold

                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    placeholder = { Text("¿A dónde quieres ir?") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resultados (${filteredTrails.size})",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredTrails) { trail ->
                TrailCard(
                    trail = trail,
                    navController = navController
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchTrailsScreenPreview() {
    val navController = rememberNavController()
    SearchTrailsScreen(navController = navController)
}
