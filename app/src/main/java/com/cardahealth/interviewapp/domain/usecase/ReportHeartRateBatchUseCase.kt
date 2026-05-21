package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.repository.ReportRepository

class ReportHeartRateBatchUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(values: List<Int>) {
        repository.reportHeartRateBatch(values)
    }
}