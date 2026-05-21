package com.cardahealth.interviewapp.domain.usecase

import com.cardahealth.interviewapp.domain.repository.APIServiceRepository

class ReportHeartRateBatchUseCase(
    private val repository: APIServiceRepository,
) {
    suspend operator fun invoke(values: List<Int>) {
        repository.reportHeartRateBatch(values)
    }
}
