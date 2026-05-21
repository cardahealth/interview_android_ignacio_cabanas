package com.cardahealth.interviewapp.ui.home

import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val isLoading: Boolean = false,
    val sensors: List<AssignedSensor> = emptyList(),
    val statuses: Map<String, StateFlow<ConnectionStatus>> = emptyMap(),
    val errorMessage: String? = null,
)
