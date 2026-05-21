package com.cardahealth.interviewapp.domain.repository

import com.cardahealth.interviewapp.domain.model.AssignedSensor

interface APIServiceRepository {
    suspend fun getAssignedSensors(): List<AssignedSensor>
    fun cachedSensor(id: String): AssignedSensor?
    suspend fun reportHeartRateBatch(values: List<Int>)
}