package com.example.alpinistapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator // Importado para el estado de carga
import androidx.compose.material3.Text
import androidx.compose.runtime.* // Importa de golpe remember, mutableStateOf y LaunchedEffect
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

@Composable
fun HomeScreen(
    navController: NavController
){
    // STEP 1: Crear las variables de estado que recordará la pantalla
    var expeditionsList by remember { mutableStateOf<List<Expedition>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // STEP 2: Lanzar la petición HTTP asíncrona a Render en cuanto cargue la pantalla
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null

            // Llamamos a tu servicio web (asegúrate de tener 'getExpeditions()' en tu AlpinistApiService)
            val response = RetrofitClient.apiService.getExpeditions()
            expeditionsList = response
        } catch (e: Exception) {
            errorMessage = "No se pudieron cargar tus próximas expediciones."
        } finally {
            isLoading = false
        }
    }

    // STEP 3: Maquetación visual de la interfaz
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2))
    ){
        // Banner Superior con Degradado (Diseño Fijo Local)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Tus próximas expediciones...",
            modifier = Modifier.padding(start = 24.dp),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // STEP 4: Control de flujo dinámico según el estado de la red
        if (isLoading) {
            // Muestra una ruedita de progreso centrada mientras descarga
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF175294))
            }
        } else if (errorMessage != null) {
            // Muestra el mensaje de error si el servidor falló o no hay internet
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
            // ¡Éxito! Dibuja la lista real con los datos dinámicos obtenidos
            LazyColumn (
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(expeditionsList) { exp ->
                    // Invocamos la ExpeditionCard optimizada (Limpia de parámetros duplicados)
                    ExpeditionCard(
                        expedition = exp,
                        navController = navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}