package com.example.alpinistapp

import android.content.Context
import android.util.Log
import com.mapbox.common.TileRegionLoadOptions
import com.mapbox.common.TileStore
import com.mapbox.geojson.CoordinateContainer
import com.mapbox.geojson.Point
import com.mapbox.maps.GlyphsRasterizationOptions
import com.mapbox.maps.OfflineManager
import com.mapbox.maps.Style
import com.mapbox.maps.TilesetDescriptorOptions

fun downloadRouteMapOffline(context: Context, routePoints: List<Point>, onComplete: (Boolean) -> Unit) {
    if (routePoints.isEmpty()) {
        onComplete(false)
        return
    }

    val lats = routePoints.map { it.latitude() }
    val lons = routePoints.map { it.longitude() }

    val minLat = lats.minOrNull() ?: 0.0
    val maxLat = lats.maxOrNull() ?: 0.0
    val minLon = lons.minOrNull() ?: 0.0
    val maxLon = lons.maxOrNull() ?: 0.0

    val bounds = com.mapbox.geojson.Polygon.fromLngLats(
        listOf(
            listOf(
                Point.fromLngLat(minLon - 0.01, minLat - 0.01),
                Point.fromLngLat(maxLon + 0.01, minLat - 0.01),
                Point.fromLngLat(maxLon + 0.01, maxLat + 0.01),
                Point.fromLngLat(minLon - 0.01, maxLat + 0.01),
                Point.fromLngLat(minLon - 0.01, minLat - 0.01)
            )
        )
    )

    val offlineManager = OfflineManager()
    val tileStore = TileStore.create()

    val tilesetDescriptor = offlineManager.createTilesetDescriptor(
        TilesetDescriptorOptions.Builder()
            .styleURI(Style.OUTDOORS)
            .minZoom(10)
            .maxZoom(16)
            .build()
    )

    val idRutaOffline = "offline_map_trail"
    val options = TileRegionLoadOptions.Builder()
        .geometry(bounds)
        .descriptors(listOf(tilesetDescriptor))
        .acceptExpired(true)
        .build()

    tileStore.loadTileRegion(
        idRutaOffline,
        options,
        { progress ->
            val porcentaje = (progress.completedResourceCount.toFloat() / progress.requiredResourceCount.toFloat()) * 100
            Log.d("MapboxOffline", "Progreso de descarga: ${porcentaje.toInt()}%")
        },
        { expected ->
            if (expected.isError) {
                Log.e("MapboxOffline", "Error al descargar mapa: ${expected.error?.message}")
                onComplete(false)
            } else {
                Log.d("MapboxOffline", "¡Mapa de la ruta descargado con éxito para uso sin conexión!")
                onComplete(true)
            }
        }
    )
}