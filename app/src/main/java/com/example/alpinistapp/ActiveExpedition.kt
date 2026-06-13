package com.example.alpinistapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ActiveExpedition(navController: NavController) {
    val context = LocalContext.current

    // Estado para almacenar los puntos cargados del GPX en segundo plano
    var routePoints by remember { mutableStateOf<List<Point>>(emptyList()) }

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(Unit) {
        val points = withContext(Dispatchers.IO) {
            parseGpx(context, "sample.gpx")
        }
        routePoints = points
    }

    AndroidView(
        factory = { ctx ->

            MapView(ctx).apply {
                mapboxMap.loadStyle(Style.OUTDOORS) {

                    location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }

                    // Marcador estático de prueba (Ej: CDMX)
                    val pointAnnotationManager = annotations.createPointAnnotationManager()
                    val staticPoint = Point.fromLngLat(-99.1332, 19.4326)
                    val staticAnnotationOptions = PointAnnotationOptions().withPoint(staticPoint)
                    pointAnnotationManager.create(staticAnnotationOptions)

                    // Obtener la última ubicación conocida del usuario para centrar la cámara por primera vez
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(it.longitude, it.latitude))
                                    .zoom(15.0)
                                    .build()
                            )

                            // Marcador en la posición actual del usuario
                            val userAnnotationOptions = PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(it.longitude, it.latitude))
                            pointAnnotationManager.create(userAnnotationOptions)
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            // El bloque 'update' reacciona de forma segura cuando cambia 'routePoints'
            if (routePoints.isNotEmpty()) {
                mapView.mapboxMap.getStyle { style ->
                    // Evitamos duplicar managers limpiando o creando ordenadamente las anotaciones
                    val polylineManager = mapView.annotations.createPolylineAnnotationManager()

                    val polyline = PolylineAnnotationOptions()
                        .withPoints(routePoints)
                        .withLineWidth(5.0)
                        .withLineColor("#175294") // Color personalizado para AlpinistApp

                    polylineManager.create(polyline)

                    // Opcional: Centrar la cámara en el inicio de la ruta cargada
                    mapView.mapboxMap.setCamera(
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