package com.example.alpinistapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.navigation.compose.rememberNavController
import com.example.alpinistapp.components.GradientButton

@Composable
fun ProfileScreen(navController: NavController) {
    // Hardcoded user data
    val userName = "Usuario Alpinista"
    val userEmail = "alpinista@example.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2F2F2F2)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        ) {
            Text(
                text = "Perfil",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF175294)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF173963)
        )

        Text(
            text = userEmail,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))
        
        ProfileInfoItem(label = "Miembro desde", value = "Enero 2024")
        ProfileInfoItem(label = "Expediciones", value = "12 completadas")

        Spacer(modifier = Modifier.weight(1f))

        GradientButton(
            text = "Cerrar Sesión",
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(text = value, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}
