package com.cardahealth.interviewapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardahealth.interviewapp.domain.service.SensorConnectionManager
import com.cardahealth.interviewapp.domain.usecase.GetAssignedSensorsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAssignedSensors: GetAssignedSensorsUseCase,
    private val connectionManager: SensorConnectionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            runCatching { getAssignedSensors() }
                .onSuccess { sensors ->
                    _uiState.value = HomeUiState(
                        sensors = sensors,
                        statuses = sensors.associate { it.id to connectionManager.statusFor(it.id) },
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = HomeUiState(errorMessage = throwable.message ?: "Unknown error")
                }
        }
    }
}
