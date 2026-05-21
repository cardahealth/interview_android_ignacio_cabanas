package com.cardahealth.interviewapp.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cardahealth.interviewapp.R
import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import com.cardahealth.interviewapp.domain.model.SensorCapability

@StringRes
private fun ConnectionStatus.labelRes(): Int = when (this) {
    ConnectionStatus.Disconnected -> R.string.sensor_state_disconnected
    ConnectionStatus.Connecting -> R.string.sensor_state_connecting
    ConnectionStatus.Connected -> R.string.sensor_state_connected
    is ConnectionStatus.Error -> R.string.sensor_state_error
}

@Composable
fun SensorCell(
    sensor: AssignedSensor,
    status: ConnectionStatus,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(sensor.id) },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${sensor.brand} · ${sensor.model}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(id = status.labelRes()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true, name = "Sensor Cell - Disconnected")
@Composable
private fun SensorCellDisconnectedPreview() {
    SensorCell(
        sensor = AssignedSensor(
            id = "sensor-1",
            brand = "Polar",
            model = "H10",
            assignmentDate = 1_700_000_000L,
            capabilities = listOf(SensorCapability.HeartRate),
        ),
        status = ConnectionStatus.Disconnected,
        onClick = {},
    )
}

@Preview(showBackground = true, name = "Sensor Cell - Connected")
@Composable
private fun SensorCellConnectedPreview() {
    SensorCell(
        sensor = AssignedSensor(
            id = "sensor-1",
            brand = "Polar",
            model = "H10",
            assignmentDate = 1_700_000_000L,
            capabilities = listOf(SensorCapability.HeartRate),
        ),
        status = ConnectionStatus.Connected,
        onClick = {},
    )
}
