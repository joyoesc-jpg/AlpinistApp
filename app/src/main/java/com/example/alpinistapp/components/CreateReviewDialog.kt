package com.example.alpinistapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alpinistapp.network.models.CreateReviewRequest

@Composable
fun CreateReviewDialog(
    registrationId: Int,
    onDismiss: () -> Unit,
    onCreate: (CreateReviewRequest) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Escribir Reseña") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Calificación", style = MaterialTheme.typography.labelLarge)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val starRating = index + 1.0
                        Icon(
                            imageVector = if (rating >= starRating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = starRating }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val request = CreateReviewRequest(
                        registration_id = registrationId,
                        rating = rating,
                        description = description
                    )
                    onCreate(request)
                },
                enabled = description.isNotBlank()
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
