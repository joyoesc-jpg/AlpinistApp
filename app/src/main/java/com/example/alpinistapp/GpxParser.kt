package com.example.alpinistapp


import android.content.Context
import com.mapbox.geojson.Point

import org.xmlpull.v1.XmlPullParser

fun parseGpx(
    context: Context,
    fileName: String
): List<Point> {

    val points = mutableListOf<Point>()

    val parser = android.util.Xml.newPullParser()

    val inputStream =
        context.assets.open(fileName)

    parser.setInput(inputStream, null)

    var eventType = parser.eventType

    while (eventType != XmlPullParser.END_DOCUMENT) {

        if (eventType == XmlPullParser.START_TAG) {

            if (parser.name == "trkpt") {

                val lat =
                    parser.getAttributeValue(null, "lat")
                        ?.toDoubleOrNull()

                val lon =
                    parser.getAttributeValue(null, "lon")
                        ?.toDoubleOrNull()

                if (lat != null && lon != null) {

                    points.add(
                        Point.fromLngLat(
                            lon,
                            lat
                        )
                    )
                }
            }
        }

        eventType = parser.next()
    }

    inputStream.close()

    return points
}