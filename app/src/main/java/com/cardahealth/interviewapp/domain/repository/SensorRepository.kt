package com.cardahealth.interviewapp.domain.repository

import com.cardahealth.interviewapp.domain.model.Sensor
import com.cardahealth.interviewapp.domain.model.SensorConnectionState
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface SensorRepository {
    suspend fun discover(): List<Sensor>
    suspend fun connect(sensorId: String): Flow<SensorConnectionState>
    fun streamHeartRate(sensorId: String): Flow<ByteBuffer>
    fun streamBattery(sensorId: String): Flow<ByteBuffer>
}