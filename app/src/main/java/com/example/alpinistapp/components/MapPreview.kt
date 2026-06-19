package com.example.alpinistapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * A component that displays a Mapbox map with a polyline track parsed from GPX data.
 *
 * @param gpxContent The XML content of the GPX file.
 * @param modifier Modifier for the map layout.
 */
@Composable
fun MapPreview(
    gpxContent: String,
    modifier: Modifier = Modifier
) {
    // Parse points only when gpxContent changes
    val points = remember(gpxContent) {
        parseGpx(gpxContent)
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().loadStyleUri(Style.OUTDOORS) {
                    if (points.isNotEmpty()) {
                        val annotationApi = annotations
                        val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()
                        
                        val polylineAnnotationOptions = PolylineAnnotationOptions()
                            .withPoints(points)
                            .withLineColor("#175294") // App primary color
                            .withLineWidth(4.0)
                        
                        polylineAnnotationManager.create(polylineAnnotationOptions)

                        // Center and zoom the camera to fit the entire track
                        val cameraOptions = getMapboxMap().cameraForCoordinates(
                            points,
                            EdgeInsets(100.0, 100.0, 100.0, 100.0),
                            null,
                            null
                        )
                        getMapboxMap().setCamera(cameraOptions)
                    }
                }
            }
        },
        modifier = modifier,
        update = { mapView ->
            // Update camera if points change (though 'remember' handles the initial state)
            if (points.isNotEmpty()) {
                val cameraOptions = mapView.getMapboxMap().cameraForCoordinates(
                    points,
                    EdgeInsets(100.0, 100.0, 100.0, 100.0),
                    null,
                    null
                )
                mapView.getMapboxMap().setCamera(cameraOptions)
            }
        }
    )
}

/**
 * Simple helper to parse latitude and longitude from a GPX XML string.
 */
private fun parseGpx(gpxContent: String): List<Point> {
    val points = mutableListOf<Point>()
    try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = ByteArrayInputStream(gpxContent.toByteArray(Charsets.UTF_8))
        val doc = builder.parse(inputStream)
        val trkpts = doc.getElementsByTagName("trkpt")
        
        for (i in 0 until trkpts.length) {
            val node = trkpts.item(i) as Element
            val lat = node.getAttribute("lat").toDoubleOrNull()
            val lon = node.getAttribute("lon").toDoubleOrNull()
            if (lat != null && lon != null) {
                points.add(Point.fromLngLat(lon, lat))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return points
}
