package com.cardahealth.interviewapp.domain.model

data class AssignedSensor(
    val id: String,
    val brand: String,
    val model: String,
    val assignmentDate: Long,
    val capabilities: List<SensorCapability>,
)