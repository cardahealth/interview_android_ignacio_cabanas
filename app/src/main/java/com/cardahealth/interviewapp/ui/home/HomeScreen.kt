package com.cardahealth.interviewapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    onSensorClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory()),
) {
    val state by viewModel.uiState.collectAsState()
    HomeContent(
        state = state,
        onRetry = viewModel::retry,
        onSensorClick = onSensorClick,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onRetry: () -> Unit,
    onSensorClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = state.errorMessage,
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
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(items = state.sensors, key = AssignedSensor::id) { sensor ->
                        val flow: StateFlow<ConnectionStatus> = remember(sensor.id, state.statuses) {
                            state.statuses[sensor.id]
                                ?: MutableStateFlow(ConnectionStatus.Disconnected)
                        }
                        val status by flow.collectAsState()
                        SensorCell(sensor = sensor, status = status, onClick = onSensorClick)
                    }
                }
            }
        }
    }
}

private val previewSensors = listOf(
    AssignedSensor(
        id = "sensor-1",
        brand = "Polar",
        model = "H10",
        assignmentDate = 1_700_000_000L,
        capabilities = listOf(SensorCapability.HeartRate),
    ),
    AssignedSensor(
        id = "sensor-2",
        brand = "Garmin",
        model = "HRM-Pro",
        assignmentDate = 1_700_000_000L,
        capabilities = listOf(SensorCapability.HeartRate, SensorCapability.Spo2),
    ),
    AssignedSensor(
        id = "sensor-3",
        brand = "Wahoo",
        model = "TICKR",
        assignmentDate = 1_700_000_000L,
        capabilities = listOf(SensorCapability.HeartRate),
    ),
)

@Preview(showBackground = true, name = "Home - Loading")
@Composable
private fun HomeContentLoadingPreview() {
    HomeContent(
        state = HomeUiState(isLoading = true),
        onRetry = {},
        onSensorClick = {},
    )
}

@Preview(showBackground = true, name = "Home - Success")
@Composable
private fun HomeContentSuccessPreview() {
    HomeContent(
        state = HomeUiState(sensors = previewSensors),
        onRetry = {},
        onSensorClick = {},
    )
}

@Preview(showBackground = true, name = "Home - Error")
@Composable
private fun HomeContentErrorPreview() {
    HomeContent(
        state = HomeUiState(errorMessage = "Couldn't load sensors"),
        onRetry = {},
        onSensorClick = {},
    )
}
