package com.cardahealth.interviewapp.domain.service

import com.cardahealth.interviewapp.domain.model.ConnectionStatus
import kotlinx.coroutines.flow.StateFlow

interface SensorConnectionManager {
    fun statusFor(sensorId: String): StateFlow<ConnectionStatus>
    fun connect(sensorId: String)
}
