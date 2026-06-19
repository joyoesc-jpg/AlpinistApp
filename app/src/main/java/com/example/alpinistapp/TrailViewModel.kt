package com.example.alpinistapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpinistapp.network.RetrofitClient
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

class TrailViewModel : ViewModel() {

    var routePoints by mutableStateOf<List<Point>>(emptyList())
        private set

    fun loadTrail(id: Int) {
        viewModelScope.launch {
            try {
                val trail = RetrofitClient.api.getTrail(id)
                routePoints = GpxParser.parse(trail.route_gpx)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
