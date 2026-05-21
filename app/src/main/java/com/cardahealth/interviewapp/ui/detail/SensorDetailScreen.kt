package com.cardahealth.interviewapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardahealth.interviewapp.R
import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import com.cardahealth.interviewapp.domain.model.SensorCapability

@Composable
fun SensorDetailScreen(
    sensorId: String,
    viewModel: SensorDetailViewModel = viewModel(factory = SensorDetailViewModelFactory(sensorId)),
) {
    val state by viewModel.uiState.collectAsState()
    SensorDetailContent(
        state = state,
        onConnectClick = viewModel::onConnectClicked,
        onRetry = viewModel::retryLoad,
    )
}

@Composable
fun SensorDetailContent(
    state: SensorDetailUiState,
    onConnectClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        when {
            state.isLoading && state.sensor == null -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.loadError != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = state.loadError,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(text = stringResource(id = R.string.retry))
                    }
                }
            }
            state.sensor != null -> {
                LoadedDetail(
                    sensor = state.sensor,
                    status = state.status,
                    batteryPercent = state.batteryPercent,
                    onConnectClick = onConnectClick,
                )
            }
        }
    }
}

@Composable
private fun LoadedDetail(
    sensor: AssignedSensor,
    status: ConnectionStatus,
    batteryPercent: Int?,
    onConnectClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "${sensor.brand} · ${sensor.model}",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(id = status.labelRes()),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(
                id = R.string.assigned_on_format,
                formatAssignmentDate(sensor.assignmentDate),
            ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = stringResource(
                id = R.string.battery_format,
                batteryPercent?.let { "$it%" } ?: stringResource(id = R.string.battery_unknown),
            ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = stringResource(id = R.string.capabilities_label),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp),
        )
        sensor.capabilities.forEach { capability ->
            Text(
                text = "• ${stringResource(id = capability.labelRes())}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (status is ConnectionStatus.Error) {
            Text(
                text = status.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Button(
            onClick = onConnectClick,
            enabled = status is ConnectionStatus.Disconnected || status is ConnectionStatus.Error,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(id = status.connectButtonLabelRes()))
        }
    }
}

private fun ConnectionStatus.labelRes(): Int = when (this) {
    ConnectionStatus.Disconnected -> R.string.sensor_state_disconnected
    ConnectionStatus.Connecting -> R.string.sensor_state_connecting
    ConnectionStatus.Connected -> R.string.sensor_state_connected
    is ConnectionStatus.Error -> R.string.sensor_state_error
}

private fun ConnectionStatus.connectButtonLabelRes(): Int = when (this) {
    ConnectionStatus.Disconnected, is ConnectionStatus.Error -> R.string.connect_button_idle
    ConnectionStatus.Connecting -> R.string.connect_button_connecting
    ConnectionStatus.Connected -> R.string.connect_button_connected
}

private val previewSensor = AssignedSensor(
    id = "sensor-1",
    brand = "Polar",
    model = "H10",
    assignmentDate = 1_700_000_000L,
    capabilities = listOf(SensorCapability.HeartRate, SensorCapability.Spo2),
)

@Preview(showBackground = true, name = "Detail - Disconnected")
@Composable
private fun DetailDisconnectedPreview() {
    SensorDetailContent(
        state = SensorDetailUiState(sensor = previewSensor),
        onConnectClick = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, name = "Detail - Connecting")
@Composable
private fun DetailConnectingPreview() {
    SensorDetailContent(
        state = SensorDetailUiState(sensor = previewSensor, status = ConnectionStatus.Connecting),
        onConnectClick = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, name = "Detail - Connected")
@Composable
private fun DetailConnectedPreview() {
    SensorDetailContent(
        state = SensorDetailUiState(
            sensor = previewSensor,
            status = ConnectionStatus.Connected,
            batteryPercent = 87,
        ),
        onConnectClick = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, name = "Detail - Error")
@Composable
private fun DetailErrorPreview() {
    SensorDetailContent(
        state = SensorDetailUiState(
            sensor = previewSensor,
            status = ConnectionStatus.Error("Sensor not connectable"),
        ),
        onConnectClick = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, name = "Detail - Load Error")
@Composable
private fun DetailLoadErrorPreview() {
    SensorDetailContent(
        state = SensorDetailUiState(loadError = "Sensor not found"),
        onConnectClick = {},
        onRetry = {},
    )
}
