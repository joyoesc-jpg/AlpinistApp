package com.example.alpinistapp

import android.graphics.Point
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

@Composable
fun TrailScreen(
    title: String,
    location: String,
    imageRes: Int,
    navController: NavController
) {

    val upcomingExpeditions = listOf(
        Expedition(
            id = 1,
            date = "15 Junio 2026",
            creator = "Cathy",
            joined = true
        ),
        Expedition(
            id = 2,
            date = "22 Junio 2026",
            creator = "Roger",
            joined = false
        ),
        Expedition(
            id = 3,
            date = "05 Julio 2026",
            creator = "Luis",
            joined = false
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF173963),
                        Color(0xFF175294),
                        Color(0xFF17635D)
                    )
                )
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "$location | 4.8 ★ | Fácil",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                TrailMapPreview("sample.gpx")
            }
        }

        item {

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {

                TrailStatsCard(
                    distance = "8.2 km",
                    elevation = "450 m",
                    estimatedTime = "4 h",
                    trailType = "Ida y vuelta",
                    recommendedGroup = "8 personas"
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Próximas expediciones",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(
            upcomingExpeditions.take(3)
        ) { expedition ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ExpeditionInfoCard(
                    date = expedition.date,
                    creator = expedition.creator,
                    joined = expedition.joined
                )
            }
        }

        item {
            TextButton(
                onClick = {
                    // TODO:
                    // navController.navigate(...)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Ver todas las expediciones",
                    color = Color.White
                )
            }
        }

        item {
            HorizontalDivider(
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                GradientButton(
                    text = "Crear expedición",
                    onClick = {}
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                GradientButton(
                    text = "Descargar mapa",
                    onClick = {}
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun ExpeditionInfoCard(
    date: String,
    creator: String,
    joined: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(
                width = if (joined) 2.dp else 0.dp,
                color = if (joined)
                    Color.White
                else
                    Color.Transparent,
                shape = RoundedCornerShape(50)
            )
            .padding(
                horizontal = 22.dp,
                vertical = 22.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Text(
                text = date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Creado por $creator",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    )
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = 10.dp
                )
        ) {

            Text(
                text = if (joined) "Unirme" else "Info",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TrailStatsCard(
    distance: String,
    elevation: String,
    estimatedTime: String,
    trailType: String,
    recommendedGroup: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Información del sendero",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        TrailStatRow(
            label = "📏 Distancia",
            value = distance
        )

        TrailStatRow(
            label = "⛰ Desnivel",
            value = elevation
        )

        TrailStatRow(
            label = "🕒 Tiempo estimado",
            value = estimatedTime
        )

        TrailStatRow(
            label = "🔁 Tipo",
            value = trailType
        )

        TrailStatRow(
            label = "👥 Grupo recomendado",
            value = recommendedGroup
        )
    }
}

@Composable
fun TrailStatRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = Color.Gray
        )
    }
}

@Composable
fun TrailMapPreview(
    gpxFileName: String
) {

    AndroidView(
        factory = { context ->

            val routePoints = parseGpx(
                context,
                gpxFileName
            )

            MapView(context).apply {

                mapboxMap.loadStyle(Style.OUTDOORS) {

                    if (routePoints.isEmpty()) return@loadStyle

                    // Ruta
                    val polylineManager =
                        annotations.createPolylineAnnotationManager()

                    polylineManager.create(
                        PolylineAnnotationOptions()
                            .withPoints(routePoints)
                    )

                    // Inicio y fin
                    val pointManager =
                        annotations.createPointAnnotationManager()

                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.first())
                    )

                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.last())
                    )

                    // Cámara
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(routePoints.first())
                            .zoom(13.0)
                            .build()
                    )
                }
            }
        }
    )
}
