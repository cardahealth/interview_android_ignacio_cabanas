package com.cardahealth.interviewapp.data.dto

import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.model.SensorCapability
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssignedSensorsResponseDto(
    val sensors: List<AssignedSensorDto>,
)

@Serializable
data class AssignedSensorDto(
    val id: String,
    val brand: String,
    val model: String,
    @SerialName("assignment_date") val assignmentDate: Long,
    val capabilities: List<String>,
) {
    fun toDomain(): AssignedSensor = AssignedSensor(
        id = id,
        brand = brand,
        model = model,
        assignmentDate = assignmentDate,
        capabilities = capabilities.mapNotNull(SensorCapability::fromRaw),
    )
}