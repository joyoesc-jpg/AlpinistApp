package com.example.alpinistapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val expeditions = listOf(
    Expedition("Cumbres del Ajusco", "Jueves 18 de junio", R.drawable.ajusco),
    Expedition("Popocatépetl", "Viernes 25 de septiembre", R.drawable.popocatepetl)
)

@Preview
@Composable
fun HomeScreen(
    navController: NavController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2))
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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

        LazyColumn (
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(expeditions){exp ->
                ExpeditionCard(
                    title = exp.title,
                    date = exp.date,
                    imageRes = exp.imageRes
                )
            }

        }
    }
}
