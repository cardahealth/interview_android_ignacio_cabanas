package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

class StreamHeartRateUseCase(
    private val repository: SensorRepository,
) {
    operator fun invoke(sensorId: String): Flow<ByteBuffer> =
        repository.streamHeartRate(sensorId)
}