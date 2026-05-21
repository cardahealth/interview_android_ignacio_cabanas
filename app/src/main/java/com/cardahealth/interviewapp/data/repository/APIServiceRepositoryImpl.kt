package com.cardahealth.interviewapp.data.repository

import com.cardahealth.apiservice.APIService
import com.cardahealth.interviewapp.data.dto.AssignedSensorDto
import com.cardahealth.interviewapp.data.dto.AssignedSensorsResponseDto
import com.cardahealth.interviewapp.data.dto.HeartRateBatchDto
import com.cardahealth.interviewapp.domain.model.AssignedSensor
import com.cardahealth.interviewapp.domain.repository.APIServiceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class APIServiceRepositoryImpl(
    private val apiService: APIService,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher,
) : APIServiceRepository {

    @Volatile
    private var cache: List<AssignedSensor> = emptyList()

    override suspend fun getAssignedSensors(): List<AssignedSensor> = withContext(ioDispatcher) {
        val raw = apiService.getAssignedSensors()
        val sensors = json.decodeFromString<AssignedSensorsResponseDto>(raw)
            .sensors
            .map(AssignedSensorDto::toDomain)
        cache = sensors
        sensors
    }

    override fun cachedSensor(id: String): AssignedSensor? =
        cache.firstOrNull { it.id == id }

    override suspend fun reportHeartRateBatch(values: List<Int>) {
        withContext(ioDispatcher) {
            val payload = json.encodeToString(HeartRateBatchDto(values))
            apiService.reportBatchHR(payload)
        }
    }
}