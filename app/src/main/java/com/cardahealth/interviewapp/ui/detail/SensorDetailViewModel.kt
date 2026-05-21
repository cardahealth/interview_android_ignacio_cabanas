package com.cardahealth.interviewapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardahealth.interviewapp.data.repository.BATTERY_BYTE_INDEX
import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import com.cardahealth.interviewapp.domain.repository.APIServiceRepository
import com.cardahealth.interviewapp.domain.repository.SensorRepository
import com.cardahealth.interviewapp.domain.service.SensorConnectionManager
import com.cardahealth.interviewapp.domain.usecase.GetAssignedSensorsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorDetailViewModel(
    private val sensorId: String,
    private val apiRepo: APIServiceRepository,
    private val getAssignedSensors: GetAssignedSensorsUseCase,
    private val sensorRepo: SensorRepository,
    private val connectionManager: SensorConnectionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorDetailUiState())
    val uiState: StateFlow<SensorDetailUiState> = _uiState.asStateFlow()

    private var batteryJob: Job? = null

    init {
        loadSensor()
        observeStatus()
    }

    fun retryLoad() {
        loadSensor()
    }

    fun onConnectClicked() {
        connectionManager.connect(sensorId)
    }

    private fun loadSensor() {
        val cached = apiRepo.cachedSensor(sensorId)
        if (cached != null) {
            _uiState.update { it.copy(sensor = cached, isLoading = false, loadError = null) }
            return
        }
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            runCatching { getAssignedSensors() }
                .onSuccess { list ->
                    val sensor = list.firstOrNull { it.id == sensorId }
                    if (sensor != null) {
                        _uiState.update {
                            it.copy(sensor = sensor, isLoading = false, loadError = null)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = "Sensor not found",
                            )
                        }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = throwable.message ?: "Couldn't load sensor",
                        )
                    }
                }
        }
    }

    private fun observeStatus() {
        viewModelScope.launch {
            connectionManager.statusFor(sensorId).collect { status ->
                _uiState.update { it.copy(status = status) }
                if (status is ConnectionStatus.Connected) {
                    startBatteryStream()
                } else {
                    stopBatteryStream()
                }
            }
        }
    }

    private fun startBatteryStream() {
        if (batteryJob?.isActive == true) return
        batteryJob = viewModelScope.launch {
            sensorRepo.streamBattery(sensorId)
                .catch { _uiState.update { state -> state.copy(batteryPercent = null) } }
                .collect { value ->
                    _uiState.update { state -> state.copy(batteryPercent = value.get(BATTERY_BYTE_INDEX).toInt() and 0xFF ) }
                }
        }
    }

    private fun stopBatteryStream() {
        batteryJob?.cancel()
        batteryJob = null
        if (_uiState.value.batteryPercent != null) {
            _uiState.update { it.copy(batteryPercent = null) }
        }
    }
}
