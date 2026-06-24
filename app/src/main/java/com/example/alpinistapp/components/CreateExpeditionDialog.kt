package com.example.alpinistapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alpinistapp.network.models.CreateExpeditionRequest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    var showDatePicker by remember { mutableStateOf(false) }
    var showMeetingTimePicker by remember { mutableStateOf(false) }
    var showDepartureTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val meetingTimePickerState = rememberTimePickerState()
    val departureTimePickerState = rememberTimePickerState()

    val dateFormatter = remember { 
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = dateFormatter.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showMeetingTimePicker) {
        TimePickerDialogHelper(
            onDismissRequest = { showMeetingTimePicker = false },
            onConfirm = {
                meetingTime = String.format("%02d:%02d", meetingTimePickerState.hour, meetingTimePickerState.minute)
                showMeetingTimePicker = false
            }
        ) {
            TimePicker(state = meetingTimePickerState)
        }
    }

    if (showDepartureTimePicker) {
        TimePickerDialogHelper(
            onDismissRequest = { showDepartureTimePicker = false },
            onConfirm = {
                departureTime = String.format("%02d:%02d", departureTimePickerState.hour, departureTimePickerState.minute)
                showDepartureTimePicker = false
            }
        ) {
            TimePicker(state = departureTimePickerState)
        }
    }

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
                ReadOnlyTextField(
                    value = date,
                    label = "Fecha (YYYY-MM-DD)",
                    onClick = { showDatePicker = true },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha") }
                )

                ReadOnlyTextField(
                    value = meetingTime,
                    label = "Hora de Encuentro (Opcional)",
                    onClick = { showMeetingTimePicker = true },
                    icon = { Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora de encuentro") }
                )

                ReadOnlyTextField(
                    value = departureTime,
                    label = "Hora de Salida (Opcional)",
                    onClick = { showDepartureTimePicker = true },
                    icon = { Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora de salida") }
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

@Composable
fun ReadOnlyTextField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = icon
        )
        // Overlay a transparent box to capture clicks and prevent keyboard from opening
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun TimePickerDialogHelper(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancelar") }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    )
}
