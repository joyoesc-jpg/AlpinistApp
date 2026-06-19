package com.example.alpinistapp.screen

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ActiveExpedition(navController: NavController) {
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    if (locationPermissionState.status.isGranted) {
        MapScreenContent()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }
            Text(
                text = "Se requiere permiso de ubicación para mostrar el mapa.",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MapScreenContent() {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().loadStyleUri(Style.OUTDOORS) {
                    // Enable location component (the blue dot/puck)
                    location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }

                    // Use the viewport plugin to follow the user
                    val viewportPlugin = viewport
                    viewportPlugin.transitionTo(
                        viewportPlugin.makeFollowPuckViewportState(
                            FollowPuckViewportStateOptions.Builder()
                                .zoom(15.0) // Set a close zoom level
                                .build()
                        )
                    )
                }
            }



        },
        modifier = Modifier.fillMaxSize()
    )
}
