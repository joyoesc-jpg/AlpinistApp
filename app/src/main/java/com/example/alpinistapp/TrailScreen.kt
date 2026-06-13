package com.example.alpinistapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Review(
    val author: String,
    val rating: Int,
    val comment: String,
    val date: String
)

@Composable
fun TrailScreen(
    routeTitle: String,
    location: String,
    imageUrl: String,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var descargando by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    val userPreferences = remember { UserPreferences(context) }
    val userName by userPreferences.userName.collectAsState(initial = "Usuario")

    // ENDPOINT: Fetch expeditions for this specific trail
    val upcomingExpeditions = remember {
        mutableStateListOf<Expedition>()
    }

    // ENDPOINT: Fetch reviews for this specific trail
    val reviews = remember {
        mutableStateListOf<Review>()
    }

    if (showCreateDialog) {
        CreateExpeditionDialog(
            trailTitle = routeTitle,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, date ->
                // ENDPOINT: POST request to create a new expedition
                upcomingExpeditions.add(
                    Expedition(
                        id = upcomingExpeditions.size + 1,
                        title = title,
                        date = date,
                        creator = userName, 
                        joined = true
                    )
                )
                showCreateDialog = false
            }
        )
    }

    if (showReviewDialog) {
        CreateReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSave = { rating, comment ->
                // ENDPOINT: POST request to save the review
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())
                reviews.add(0, Review(userName, rating, comment, "Hoy ($currentDate)"))
                showReviewDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF173963), Color(0xFF175294), Color(0xFF17635D))
                )
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = routeTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = routeTitle, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                val trailRating = if (reviews.isEmpty()) "--" else String.format("%.1f", reviews.map { it.rating }.average())
                Spacer(modifier = Modifier.height(8.dp))
                // ENDPOINT: Difficulty should come from the trail data
                Text(text = "$location | $trailRating ★ | --", color = Color.White, fontSize = 14.sp)
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
                // ENDPOINT: Load the specific GPX file for this trail
                TrailMapPreview("sample.gpx")
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                // ENDPOINT: Trail stats should be fetched from the API
                TrailStatsCard(
                    distance = "-- km",
                    elevation = "-- m",
                    estimatedTime = "-- h",
                    trailType = "--",
                    recommendedGroup = "-- personas"
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Próximas expediciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (upcomingExpeditions.isEmpty()) {
            item {
                Text(
                    text = "No hay expediciones próximas.",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(upcomingExpeditions.take(3)) { expedition ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ExpeditionInfoCard(date = expedition.date, creator = expedition.creator, joined = expedition.joined)
                }
            }
        }

        item {
            TextButton(
                onClick = { navController.navigate("expedition") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(text = "Ver todas las expediciones", color = Color.White)
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                GradientButton(text = "Crear expedición", onClick = { showCreateDialog = true })
                Spacer(modifier = Modifier.height(12.dp))
                GradientButton(
                    text = if (descargando) "Descargando..." else "Descargar mapa",
                    onClick = {
                        if (!descargando) {
                            descargando = true
                            scope.launch(Dispatchers.IO) {
                                // ENDPOINT: Download GPX and map tiles for offline use
                                val puntosDeRuta = parseGpx(context, "sample.gpx")
                                downloadRouteMapOffline(context, puntosDeRuta) { _ ->
                                    descargando = false
                                }
                            }
                        }
                    }
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Reseñas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showReviewDialog = true }) {
                        Text(text = "Añadir reseña", color = Color(0xffff9b3d))
                    }
                }
            }
        }

        if (reviews.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay reseñas para esta ruta.",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(reviews) { review ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ReviewCard(review)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun CreateReviewDialog(
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Añadir reseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Calificación:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (index < rating) Color(0xFFFFB400) else Color.Gray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = index + 1 }
                        )
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSave(rating, comment)
                    }
                }
            ) {
                Text("Publicar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CreateExpeditionDialog(
    trailTitle: String,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var expeditionTitle by remember { mutableStateOf("Expedición a $trailTitle") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Crear Nueva Expedición", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = expeditionTitle,
                    onValueChange = { expeditionTitle = it },
                    label = { Text("Título de la expedición") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (ej. 15 Junio 2026)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (expeditionTitle.isNotBlank() && date.isNotBlank()) {
                        onCreate(expeditionTitle, date)
                    }
                }
            ) {
                Text("Crear", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ReviewCard(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = review.author, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(text = review.date, color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < review.rating) Color(0xFFFFB400) else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = review.comment, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun ExpeditionInfoCard(date: String, creator: String, joined: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(
                width = if (joined) 2.dp else 0.dp,
                color = if (joined) Color.White else Color.Transparent,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = date, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Creado por $creator", fontSize = 12.sp, color = Color.Gray)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(Color(0xFF175294), Color(0xFF17635D))))
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(text = if (joined) "Unirme" else "Info", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TrailStatsCard(distance: String, elevation: String, estimatedTime: String, trailType: String, recommendedGroup: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Información del sendero", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        TrailStatRow(label = "📏 Distancia", value = distance)
        TrailStatRow(label = "⛰ Desnivel", value = elevation)
        TrailStatRow(label = "🕒 Tiempo estimado", value = estimatedTime)
        TrailStatRow(label = "🔁 Tipo", value = trailType)
        TrailStatRow(label = "👥 Grupo recomendado", value = recommendedGroup)
    }
}

@Composable
fun TrailStatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontWeight = FontWeight.Medium)
        Text(text = value, color = Color.Gray)
    }
}

@Composable
fun TrailMapPreview(gpxFileName: String) {
    AndroidView<MapView>(
        factory = { context ->
            val routePoints = parseGpx(context, gpxFileName)
            MapView(context).apply {
                mapboxMap.loadStyle(Style.OUTDOORS) {
                    if (routePoints.isEmpty()) return@loadStyle
                    val polylineManager = annotations.createPolylineAnnotationManager()
                    polylineManager.create(
                        PolylineAnnotationOptions()
                            .withPoints(routePoints)
                            .withLineColor("#175294")
                            .withLineWidth(4.0)
                    )

                    val pointManager = annotations.createPointAnnotationManager()
                    
                    // HIGHLIGHTING END POINTS
                    // Start Point (INICIO)
                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.first())
                            .withTextField("INICIO")
                            .withTextColor("#2E7D32") // Green
                    )
                    
                    // End Point (FIN)
                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.last())
                            .withTextField("FIN")
                            .withTextColor("#C62828") // Red
                    )

                    mapboxMap.setCamera(
                        CameraOptions.Builder().center(routePoints.first()).zoom(13.0).build()
                    )
                }
            }
        }
    )
}
