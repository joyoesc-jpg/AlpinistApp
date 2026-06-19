package com.example.alpinistapp.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alpinistapp.network.models.ExpeditionCardResponse

@Composable
fun ExpeditionCard(
    expedition: ExpeditionCardResponse,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Encode the date since it might contain slashes or spaces
                val encodedDate = Uri.encode(expedition.date)
                
                // Matches the route in MainActivity: expedition_detail/{expeditionId}/{trailId}/{date}
                navController.navigate("expedition_detail/${expedition.expedition_id}/${expedition.trail_id}/$encodedDate")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = expedition.image,
                contentDescription = expedition.trail_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = expedition.trail_name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF173963)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expedition.date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
