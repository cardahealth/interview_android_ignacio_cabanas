package com.cardahealth.interviewapp.ui.detail

import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.model.ConnectionStatus

data class SensorDetailUiState(
    val sensor: AssignedSensor? = null,
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val status: ConnectionStatus = ConnectionStatus.Disconnected,
    val batteryPercent: Int? = null,
)
