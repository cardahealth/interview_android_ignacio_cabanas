package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.model.SensorConnectionState
import com.cardahealth.interviewapp.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow

class ConnectToSensorUseCase(
    private val repository: SensorRepository,
) {
    suspend operator fun invoke(sensorId: String): Flow<SensorConnectionState> =
        repository.connect(sensorId)
}