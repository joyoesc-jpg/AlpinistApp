package com.example.alpinistapp

import com.mapbox.geojson.Point
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object GpxParser {

    fun parse(gpx: String): List<Point> {

        val points = mutableListOf<Point>()

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()

        parser.setInput(StringReader(gpx))

        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (
                eventType == XmlPullParser.START_TAG &&
                parser.name == "trkpt"
            ) {

                val lat = parser.getAttributeValue(null, "lat").toDouble()
                val lon = parser.getAttributeValue(null, "lon").toDouble()

                points.add(
                    Point.fromLngLat(lon, lat)
                )
            }

            eventType = parser.next()
        }

        return points
    }
}
