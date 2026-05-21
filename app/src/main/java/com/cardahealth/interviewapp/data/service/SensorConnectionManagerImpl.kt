package com.cardahealth.interviewapp.data.service

import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import com.cardahealth.interviewapp.domain.model.SensorConnectionState
import com.cardahealth.interviewapp.domain.service.SensorConnectionManager
import com.cardahealth.interviewapp.domain.usecase.ConnectToSensorUseCase
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorConnectionManagerImpl(
    private val connectToSensor: ConnectToSensorUseCase,
    private val scope: CoroutineScope,
) : SensorConnectionManager {

    private val flows = ConcurrentHashMap<String, MutableStateFlow<ConnectionStatus>>()
    private val jobs = ConcurrentHashMap<String, Job>()

    override fun statusFor(sensorId: String): StateFlow<ConnectionStatus> =
        flowFor(sensorId).asStateFlow()

    override fun connect(sensorId: String) {
        val flow = flowFor(sensorId)
        val current = flow.value
        if (current is ConnectionStatus.Connecting || current is ConnectionStatus.Connected) return

        jobs[sensorId]?.cancel()
        jobs[sensorId] = scope.launch {
            flow.value = ConnectionStatus.Connecting
            try {
                connectToSensor(sensorId).collect { raw ->
                    flow.value = when (raw) {
                        SensorConnectionState.Disconnected -> ConnectionStatus.Disconnected
                        SensorConnectionState.Connecting -> ConnectionStatus.Connecting
                        SensorConnectionState.Connected -> ConnectionStatus.Connected
                    }
                }
            } catch (e: IllegalStateException) {
                flow.value = ConnectionStatus.Error(e.message ?: "Connection error")
            } catch (e: IllegalArgumentException) {
                flow.value = ConnectionStatus.Error(e.message ?: "Sensor not connectable")
            }
        }
    }

    private fun flowFor(sensorId: String): MutableStateFlow<ConnectionStatus> =
        flows.getOrPut(sensorId) { MutableStateFlow(ConnectionStatus.Disconnected) }
}