package com.example.alpinistapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpeditionScreen(
    title: String,
    date: String,
    imageRes: Int
){
    val listState = rememberLazyListState()

    Box{
        LazyColumn(
            state = listState
        ) {
            item{
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            item {
                DetailContent(title, date)
            }
        }

        CollapsingHeader(
            title = title,
            date = date,
            listState = listState
        )
    }
}

@Composable
fun CollapsingHeader (
    title: String,
    date: String,
    listState: LazyListState
){
    val showHeader = listState.firstVisibleItemIndex > 0

    AnimatedVisibility(visible = showHeader){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF173963),
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    )
                )
                .padding(16.dp)
        ){
            Column{
                Text(text = title, color = Color.White, fontSize = 18.sp)
                Text(text = date, color = Color.White)
            }
        }
    }
}

@Composable
fun DetailContent(title: String, date: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1F4A7C),
                        Color(0xFF1F6A6D)
                    )
                )
            )
            .padding(16.dp)
    ) {

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(text = date, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton("Información del sendero", {})

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton("Chat", {})

        Spacer(modifier = Modifier.height(16.dp))

        Text("Personas:", color = Color.White)

        HorizontalDivider(color = Color.White)

        Spacer(modifier = Modifier.height(8.dp))

        ParticipantCard("Alfonso Obregon", "AlfOber12")
        ParticipantCard("Rogelio Juarez", "Roger")
        ParticipantCard("Catalina Lopez", "Cathy")
    }
}

@Composable
fun ParticipantCard(name: String, user: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(50))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(name)
            Text(user, color = Color.Gray)
        }
    }
}
