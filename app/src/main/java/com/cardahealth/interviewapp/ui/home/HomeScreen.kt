package com.cardahealth.interviewapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory()),
) {
    val state by viewModel.uiState.collectAsState()
    val stateLabel = state.connectionState?.let { stringResource(id = it.value) } ?: "—"
    val sensorId = state.sensor?.id ?: "—"
    val lastHr = state.lastHeartRate?.toString() ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Sensor: $sensorId")
        Text(text = "Sensor State: $stateLabel", modifier = Modifier.padding(top = 8.dp))
        Text(text = "Last HR: $lastHr BPM", modifier = Modifier.padding(top = 8.dp))
        Text(text = "Batches sent: ${state.batchesSent}", modifier = Modifier.padding(top = 4.dp))
        Button(
            onClick = viewModel::onConnectClicked,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "Connect")
        }
        state.errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 12.dp))
        }
    }
}