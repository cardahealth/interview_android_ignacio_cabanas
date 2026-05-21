package com.cardahealth.interviewapp.data.repository

import com.cardahealth.interviewapp.domain.model.Sensor
import com.cardahealth.interviewapp.domain.model.SensorConnectionState
import com.cardahealth.interviewapp.domain.repository.SensorRepository
import com.cardahealth.sensorservice.SensorService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val HEART_RATE_BYTE_INDEX = 1

class SensorRepositoryImpl(
    private val sensorService: SensorService,
    private val ioDispatcher: CoroutineDispatcher,
) : SensorRepository {

    override suspend fun discover(): List<Sensor> = withContext(ioDispatcher) {
        sensorService.discover().map(::Sensor)
    }

    override suspend fun connect(sensorId: String): Flow<SensorConnectionState> =
        withContext(ioDispatcher) {
            sensorService.connect(sensorId)
                .map(SensorConnectionState::fromRaw)
                .flowOn(ioDispatcher)
        }

    override fun streamHeartRate(sensorId: String): Flow<Int> =
        sensorService.getDataFlow(sensorId)
            .map { buffer -> buffer.get(HEART_RATE_BYTE_INDEX).toInt() and 0xFF }
            .flowOn(ioDispatcher)
}