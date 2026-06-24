package com.example.alpinistapp.screen

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.common.TileStore
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
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
    val tileStore = remember { TileStore.create() }
    var downloadedRegions by remember { mutableStateOf<List<Polygon>>(emptyList()) }

    LaunchedEffect(Unit) {
        tileStore.getAllTileRegions { expected ->
            if (expected.isValue) {
                val regions = expected.value ?: emptyList()
                val polygons = regions.mapNotNull { region ->
                    // Geometry is often stored as metadata or we can get it from the region
                    // In TileStore, we can get the geometry used for the load request
                    // though it might not be directly in the Region object depending on version.
                    // However, we can try to get it if available or just draw the bounds.
                    // For this implementation, we assume we want to visualize them.
                    // Note: TileRegion doesn't directly expose geometry in some versions without fetching metadata.
                    null // Placeholder for actual geometry extraction logic if needed
                }
                // Since TileRegion API varies, let's fetch them if they have descriptors/metadata
            }
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().loadStyleUri(Style.OUTDOORS) {
                    // Enable location component
                    location.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }

                    // Follow the user
                    val viewportPlugin = viewport
                    viewportPlugin.transitionTo(
                        viewportPlugin.makeFollowPuckViewportState(
                            FollowPuckViewportStateOptions.Builder()
                                .zoom(15.0)
                                .build()
                        )
                    )

                    // Draw downloaded regions
                    val annotationApi = annotations
                    val polygonAnnotationManager = annotationApi.createPolygonAnnotationManager()

                    tileStore.getAllTileRegions { expected ->
                        if (expected.isValue) {
                            expected.value?.forEach { region ->
                                // Mapbox TileStore regions don't easily expose the original geometry 
                                // unless we stored it in metadata or use the OfflineManager to query.
                                // But we can try to show them if we had their bounds.
                            }
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
