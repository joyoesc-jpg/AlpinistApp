package com.example.alpinistapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

import com.mapbox.maps.plugin.annotation.annotations

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ActiveExpedition(navController: NavController) {

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    AndroidView(
        factory = { context ->
            val mapView = MapView(context)

            mapView.mapboxMap.loadStyle(Style.OUTDOORS) {

                val annotationApi = mapView.annotations

                val pointAnnotationManager =
                    annotationApi.createPointAnnotationManager()

                mapView.location.updateSettings {
                    enabled = true
                    pulsingEnabled = true
                }

                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)

                val routePoints =
                    parseGpx(
                        context,
                        "sample.gpx"
                    )

                val polylineManager =
                    mapView.annotations
                        .createPolylineAnnotationManager()

                val polyline =
                    PolylineAnnotationOptions()
                        .withPoints(routePoints)

                polylineManager.create(polyline)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            mapView.mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .center(
                                        Point.fromLngLat(
                                            it.longitude,
                                            it.latitude
                                        )
                                    )
                                    .zoom(16.0)
                                    .build()
                            )

                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withPoint(
                                    Point.fromLngLat(
                                        it.longitude,
                                        it.latitude
                                    )
                                )
                            pointAnnotationManager.create(pointAnnotationOptions)
                        }
                    }
                val point = Point.fromLngLat(
                    -99.1332,
                    19.4326
                )

                val pointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(point)

                pointAnnotationManager.create(pointAnnotationOptions)

            }
            mapView
        }
    )
}
