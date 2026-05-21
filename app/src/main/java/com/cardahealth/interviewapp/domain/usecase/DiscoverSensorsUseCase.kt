package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.model.Sensor
import com.cardahealth.interviewapp.domain.repository.SensorRepository

class DiscoverSensorsUseCase(
    private val repository: SensorRepository,
) {
    suspend operator fun invoke(): List<Sensor> = repository.discover()
}