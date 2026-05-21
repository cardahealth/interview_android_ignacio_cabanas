package com.cardahealth.interviewapp.domain.service

import kotlinx.coroutines.flow.StateFlow

data class HeartRateCollectionState(
    val isRunning: Boolean = false,
    val lastHeartRate: Int? = null,
    val batchesSent: Int = 0,
    val errorMessage: String? = null,
)

interface HeartRateCollectionService {
    val state: StateFlow<HeartRateCollectionState>
    fun start(sensorId: String)
    fun stop()
}