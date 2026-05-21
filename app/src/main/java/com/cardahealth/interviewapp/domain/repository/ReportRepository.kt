package com.cardahealth.interviewapp.domain.repository

interface ReportRepository {
    suspend fun reportHeartRateBatch(values: List<Int>)
}