package com.cardahealth.interviewapp.data.repository

import com.cardahealth.apiservice.APIService
import com.cardahealth.interviewapp.data.dto.HeartRateBatchDto
import com.cardahealth.interviewapp.domain.repository.ReportRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReportRepositoryImpl(
    private val apiService: APIService,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher,
) : ReportRepository {

    override suspend fun reportHeartRateBatch(values: List<Int>) {
        withContext(ioDispatcher) {
            val payload = json.encodeToString(HeartRateBatchDto(values))
            apiService.reportBatchHR(payload)
        }
    }
}