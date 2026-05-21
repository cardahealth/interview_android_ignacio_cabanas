package com.cardahealth.interviewapp.ui.home

import com.cardahealth.interviewapp.domain.model.Sensor
import com.cardahealth.interviewapp.domain.model.SensorConnectionState

data class HomeUiState(
    val isLoading: Boolean = false,
    val sensor: Sensor? = null,
    val connectionState: SensorConnectionState? = null,
    val lastHeartRate: Int? = null,
    val batchesSent: Int = 0,
    val errorMessage: String? = null,
)