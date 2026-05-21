package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.repository.APIServiceRepository

class GetAssignedSensorsUseCase(
    private val repository: APIServiceRepository,
) {
    suspend operator fun invoke(): List<AssignedSensor> = repository.getAssignedSensors()
}