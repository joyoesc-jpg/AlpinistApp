package com.example.alpinistapp

import TrailDetails
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.annotations.SerializedName
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
import android.util.Log
import com.example.alpinistapp.TrailDetailsResponse
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import com.example.alpinistapp.CreateExpeditionRequest
import com.example.alpinistapp.ExpeditionResponse
import com.example.alpinistapp.JoinExpeditionRequest
import com.example.alpinistapp.ApiResponse
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalLocale


data class Review(
    @SerializedName("author") val author: String?,
    val rating: Int,
    val comment: String,
    val date: String?
)

@Composable
fun TrailScreen(
    routeTitle: String,
    trailId: Int?,
    location: String,
    imageUrl: String,
    navController: NavController,
    viewModel: TrailViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var descargando by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    var trailDetails by remember { mutableStateOf<TrailDetailsResponse?>(null) }
    var isLoadingDetails by remember { mutableStateOf(true) }
    var detailsError by remember { mutableStateOf<String?>(null) }

    val userPreferences = remember { UserPreferences(context) }
    val userName by userPreferences.userName.collectAsState(initial = "Usuario")
    val userId by userPreferences.userId.collectAsState(initial = 0)

    val reviews by viewModel.reviews.collectAsState()

    var expeditionsList by remember { mutableStateOf<List<ExpeditionResponse>>(emptyList()) }
    var isLoadingExpeditions by remember { mutableStateOf(true) }
    var isCreatingExpedition by remember { mutableStateOf(false) }

    var reloadExpeditions by remember { mutableStateOf(false) }

    LaunchedEffect(trailId) {
        viewModel.fetchReviews(trailId)
    }

    LaunchedEffect(trailId) {
        if (trailId != null && trailId > 0) {
            isLoadingDetails = true
            detailsError = null
            try {
                val details = RetrofitClient.apiService.getTrailDetails(trailId)
                trailDetails = details
                Log.d("TRAIL_DETAILS", "Detalles cargados: $details")
            } catch (e: Exception) {
                detailsError = "No se pudieron cargar los detalles del sendero"
                Log.e("TRAIL_DETAILS", "Error cargando detalles: ${e.message}", e)
            } finally {
                isLoadingDetails = false
            }
        } else {
            isLoadingDetails = false
            detailsError = "ID de sendero inválido"
        }
    }


    LaunchedEffect(trailId, reloadExpeditions) {
        if (trailId != null && trailId > 0) {
            isLoadingExpeditions = true
            try {
                val expeditions = RetrofitClient.apiService.getExpeditionsByTrail(
                    trailId = trailId
                )
                expeditionsList = expeditions
                Log.d("EXPEDICIONES", "Cargadas ${expeditions.size} expediciones")
            } catch (e: Exception) {
                Log.e("EXPEDICIONES", "Error cargando: ${e.message}", e)
            } finally {
                isLoadingExpeditions = false
            }
        }
    }

    if (showCreateDialog) {
        CreateExpeditionDialog(
            trailTitle = routeTitle,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, date, time ->
                scope.launch {
                    try {
                        val request = CreateExpeditionRequest(
                            title = title,
                            date = date,
                            time = time,
                            trailId = trailId ?: 0,
                            creatorId = userId
                        )

                        val response = RetrofitClient.apiService.createExpedition(request)
                        if (response.success) {
                            Toast.makeText(context, "✅ ${response.message}", Toast.LENGTH_SHORT).show()
                            showCreateDialog = false
                            // Forzar recarga de expediciones
                            reloadExpeditions = !reloadExpeditions
                        } else {
                            Toast.makeText(context, "❌ ${response.message}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("CREAR_EXP", "Error: ${e.message}", e)
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    if (showReviewDialog) {
        CreateReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSave = { rating, comment ->
                if (trailId != null) {
                    if (userId != 0 && trailId > 0) {
                        viewModel.addReview(
                            userId = userId,
                            trailId = trailId,
                            rating = rating,
                            comment = comment,
                            onSuccess = {
                                showReviewDialog = false
                                Toast.makeText(context, "Reseña publicada con éxito", Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        val errorMsg = if (trailId <= 0) "Error: ID de ruta inválido ($trailId)"
                        else "Error: Sesión no encontrada"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
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
                // Nombre del sendero
                Text(text = routeTitle, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)

                // Calificación promedio (de las reseñas)
                val trailRating = if (reviews.isEmpty()) {
                    "Sin calificaciones"
                } else {
                    val promedio = reviews.map { it.rating }.average()
                    String.format("%.1f ★", promedio)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ubicación y dificultad (desde trailDetails)
                val difficultyText = if (trailDetails != null && trailDetails!!.routeType.isNotBlank()) {
                    trailDetails!!.routeType
                } else if (isLoadingDetails) {
                    "Cargando..."
                } else {
                    "Dificultad no especificada"
                }

                Text(
                    text = "$location | $trailRating | $difficultyText",
                    color = Color.White.copy(alpha = 0.9f),
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
                // ENDPOINT: Load the specific GPX file for this trail
                TrailMapPreview("sample.gpx")
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                when {
                    isLoadingDetails -> {
                        // ... código de carga
                    }
                    detailsError != null -> {
                        // ... código de error
                    }
                    trailDetails != null -> {
                        TrailStatsCard(
                            distance = "${trailDetails!!.distance} km",
                            elevation = "${trailDetails!!.elevationGain} m",
                            estimatedTime = "${trailDetails!!.estimatedTime} horas",
                            trailType = trailDetails!!.routeType,
                            recommendedGroup = "${trailDetails!!.recommendedGroup} personas"
                        )
                    }
                    else -> {
                        Text("No hay información disponible")
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Próximas expediciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        when {
            isLoadingExpeditions -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
            expeditionsList.isEmpty() -> {
                item {
                    Text(
                        text = "No hay expediciones próximas. ¡Crea la primera!",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                items(expeditionsList.take(3)) { expedition ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ExpeditionInfoCard(
                            expedition = expedition,
                            currentUserId = userId,
                            userName = userName,
                            onJoin = { expId ->
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.apiService.joinExpedition(
                                            expeditionId = expId,
                                            request = JoinExpeditionRequest(userId = userId)
                                        )
                                        if (response.success) {
                                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                            // Recargar expediciones
                                            val updated = RetrofitClient.apiService.getExpeditionsByTrail(
                                                trailId = trailId ?: 0
                                            )
                                            expeditionsList = updated
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        )
                    }
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
                    TextButton(onClick = {
                        Log.d("DEBUG_TRAIL", "Abriendo diálogo para TrailID: $trailId")
                        showReviewDialog = true }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpeditionDialog(
    trailTitle: String,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var expeditionTitle by remember { mutableStateOf("Expedición a $trailTitle") }

    // Fecha
    val calendar = Calendar.getInstance()
    val format = SimpleDateFormat("yyyy-MM-dd", LocalLocale.current.platformLocale)
    var selectedDate by remember {
        mutableStateOf(format.format(calendar.time))  // ← fecha de hoy por defecto
    }
    var selectedDateMillis by remember { mutableStateOf(calendar.timeInMillis) }

    // Hora
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    // Estado para controlar los diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Formatear fecha cuando cambia
    LaunchedEffect(selectedDateMillis) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = format.format(java.util.Date(selectedDateMillis))
    }

    // Efecto para mostrar DatePicker
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDateMillis = calendar.timeInMillis
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar") { _, _ ->
                    showDatePicker = false
                }
                show()
            }
        }
    }

    // Efecto para mostrar TimePicker
    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    showTimePicker = false
                },
                selectedHour,
                selectedMinute,
                true
            ).apply {
                setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancelar") { _, _ ->
                    showTimePicker = false
                }
                show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Expedición", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = expeditionTitle,
                    onValueChange = { expeditionTitle = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                // ✅ CORRECCIÓN — envolver el TextField en un Box con el clickable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }  // ← el Box sí captura el toque
                ) {
                    OutlinedTextField(
                        value = if (selectedDate.isEmpty()) "Seleccionar fecha" else selectedDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,          // ← deshabilitar para que no robe el foco
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha") },
                        trailingIcon = { Text("📅", fontSize = 20.sp) },
                        colors = OutlinedTextFieldDefaults.colors(  // ← evitar que se vea "gris de deshabilitado"
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                ) {
                    OutlinedTextField(
                        value = String.format("%02d:%02d", selectedHour, selectedMinute),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hora") },
                        trailingIcon = { Text("⏰", fontSize = 20.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (expeditionTitle.isNotBlank() && selectedDate.isNotBlank()) {
                        val timeString = String.format("%02d:%02d:00", selectedHour, selectedMinute)
                        onCreate(expeditionTitle, selectedDate, timeString)
                        onDismiss()
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
            Text(
                text = review.author ?: "Usuario anónimo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = review.date ?: "",
                color = Color.Gray,
                fontSize = 12.sp
            )
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
fun ExpeditionInfoCard(
    expedition: ExpeditionResponse,
    userName: String,
    currentUserId: Int,
    onJoin: (Int) -> Unit
) {
    val isCreator = expedition.creatorId == currentUserId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = expedition.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${expedition.date} a las ${expedition.time}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "Creado por ${expedition.creatorName} • ${expedition.memberCount} participantes",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .then(
                    if (expedition.isJoined || isCreator) {
                        Modifier.background(Color(0xFF4CAF50))
                    } else {
                        Modifier.background(
                            Brush.horizontalGradient(listOf(Color(0xFF175294), Color(0xFF17635D)))
                        )
                    }
                )
                .clickable(enabled = !isCreator && !expedition.isJoined) {
                    if (!isCreator && !expedition.isJoined) {
                        onJoin(expedition.id)
                    }
                }
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = when {
                    isCreator -> "Creador"
                    expedition.isJoined -> "Unido"
                    else -> "Unirme"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
