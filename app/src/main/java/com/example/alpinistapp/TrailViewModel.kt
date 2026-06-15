package com.example.alpinistapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrailViewModel : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun fetchReviews(trailId: Int?) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTrailReviews(trailId)
                _reviews.value = response
            } catch (e: Exception) {
                Log.e("TrailViewModel", "Error fetching reviews: ${e.message}")
            }
        }
    }

    fun addReview(
        userId: Int,
        trailId: Int?,
        rating: Int,
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("TrailViewModel", "Intentando publicar reseña - UserID: $userId, TrailID: $trailId")
                
                if (userId == 0) {
                    onError("Sesión no válida. Por favor, inicia sesión de nuevo.")
                    return@launch
                }

                val request = ReviewRequest(userId, trailId, rating, comment)
                RetrofitClient.apiService.postReview(request)
                
                Log.d("TrailViewModel", "Reseña publicada con éxito")
                
                // Refrescamos la lista después de publicar
                fetchReviews(trailId)
                onSuccess()
            } catch (e: Exception) {
                Log.e("TrailViewModel", "Error al guardar reseña: ${e.message}", e)
                onError("Error de conexión: ${e.localizedMessage ?: "Inténtalo de nuevo"}")
            }
        }
    }

    fun fetchTrails() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTrails()
                // Imprime esto en el Logcat
                Log.d("DEBUG_DATA", "Primer elemento: ${response.firstOrNull()}")
            } catch (e: Exception) {
                Log.e("DEBUG_DATA", "Error: ${e.message}")
            }
        }
    }
}

