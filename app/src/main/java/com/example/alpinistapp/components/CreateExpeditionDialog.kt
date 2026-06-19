package com.example.alpinistapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alpinistapp.network.models.CreateExpeditionRequest

@Composable
fun CreateExpeditionDialog(
    trailId: Int,
    creatorId: Int,
    onDismiss: () -> Unit,
    onCreate: (CreateExpeditionRequest) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var meetingTime by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Crear Nueva Expedición") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = meetingTime,
                    onValueChange = { meetingTime = it },
                    label = { Text("Hora de Encuentro (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = departureTime,
                    onValueChange = { departureTime = it },
                    label = { Text("Hora de Salida (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val request = CreateExpeditionRequest(
                        trail_id = trailId,
                        creator_id = creatorId,
                        date = date,
                        meeting_time = meetingTime.ifBlank { null },
                        departure_time = departureTime.ifBlank { null }
                    )
                    onCreate(request)
                },
                enabled = date.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
