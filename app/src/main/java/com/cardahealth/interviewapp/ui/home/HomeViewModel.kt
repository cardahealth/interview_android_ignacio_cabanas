package com.cardahealth.interviewapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardahealth.interviewapp.domain.model.SensorConnectionState
import com.cardahealth.interviewapp.domain.service.HeartRateCollectionService
import com.cardahealth.interviewapp.domain.usecase.ConnectToSensorUseCase
import com.cardahealth.interviewapp.domain.usecase.DiscoverSensorsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val discoverSensors: DiscoverSensorsUseCase,
    private val connectToSensor: ConnectToSensorUseCase,
    private val heartRateCollectionService: HeartRateCollectionService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var connectionJob: Job? = null

    init {
        loadFirstSensor()
        observeHeartRateCollection()
    }

    private fun loadFirstSensor() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { discoverSensors().firstOrNull() }
                .onSuccess { sensor ->
                    _uiState.value = _uiState.value.copy(isLoading = false, sensor = sensor)
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message,
                    )
                }
        }
    }

    private fun observeHeartRateCollection() {
        viewModelScope.launch {
            heartRateCollectionService.state.collect { collectionState ->
                _uiState.value = _uiState.value.copy(
                    lastHeartRate = collectionState.lastHeartRate,
                    batchesSent = collectionState.batchesSent,
                    errorMessage = collectionState.errorMessage ?: _uiState.value.errorMessage,
                )
            }
        }
    }

    fun onConnectClicked() {
        val sensor = _uiState.value.sensor ?: return
        if (connectionJob?.isActive == true) return

        connectionJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = null)
            runCatching { connectToSensor(sensor.id) }
                .onSuccess { flow ->
                    flow
                        .onEach { state ->
                            _uiState.value = _uiState.value.copy(connectionState = state)
                            if (state == SensorConnectionState.Connected) {
                                heartRateCollectionService.start(sensor.id)
                            }
                        }
                        .catch { throwable ->
                            _uiState.value = _uiState.value.copy(errorMessage = throwable.message)
                        }
                        .collect()
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(errorMessage = throwable.message)
                }
        }
    }
}