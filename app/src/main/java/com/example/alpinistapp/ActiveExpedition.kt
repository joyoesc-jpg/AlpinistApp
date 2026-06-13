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

    // API ENDPOINT: Load real route points from specific expedition GPX
    var routePoints by remember { mutableStateOf<List<Point>>(emptyList()) }

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(Unit) {
        val points = withContext(Dispatchers.IO) {
            // API ENDPOINT: Fetch GPX from server or cache
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

                    // Marcadores de punto
                    val pointAnnotationManager = annotations.createPointAnnotationManager()

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
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            if (routePoints.isNotEmpty()) {
                mapView.mapboxMap.getStyle { style ->
                    val polylineManager = mapView.annotations.createPolylineAnnotationManager()
                    val pointManager = mapView.annotations.createPointAnnotationManager()

                    // Dibujar la ruta
                    val polyline = PolylineAnnotationOptions()
                        .withPoints(routePoints)
                        .withLineWidth(5.0)
                        .withLineColor("#175294")

                    polylineManager.create(polyline)

                    // HIGHLIGHTING END POINTS: Marking Start (Green) and End (Red)
                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.first())
                            .withTextField("INICIO")
                            .withTextColor("#2E7D32")
                    )
                    pointManager.create(
                        PointAnnotationOptions()
                            .withPoint(routePoints.last())
                            .withTextField("FIN")
                            .withTextColor("#C62828")
                    )

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
