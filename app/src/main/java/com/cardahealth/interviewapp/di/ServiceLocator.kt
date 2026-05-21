package com.cardahealth.interviewapp.di

import com.cardahealth.apiservice.APIService
import com.cardahealth.interviewapp.data.repository.ReportRepositoryImpl
import com.cardahealth.interviewapp.data.repository.SensorRepositoryImpl
import com.cardahealth.interviewapp.data.service.HeartRateCollectionServiceImpl
import com.cardahealth.interviewapp.domain.repository.ReportRepository
import com.cardahealth.interviewapp.domain.repository.SensorRepository
import com.cardahealth.interviewapp.domain.service.HeartRateCollectionService
import com.cardahealth.interviewapp.domain.usecase.ConnectToSensorUseCase
import com.cardahealth.interviewapp.domain.usecase.DiscoverSensorsUseCase
import com.cardahealth.interviewapp.domain.usecase.ReportHeartRateBatchUseCase
import com.cardahealth.interviewapp.domain.usecase.StreamHeartRateUseCase
import com.cardahealth.sensorservice.SensorService
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlin.getValue

object ServiceLocator {

    private val json: Json by lazy { Json { ignoreUnknownKeys = true } }

    private val sensorService: SensorService by lazy { SensorService() }
    private val apiService: APIService by lazy { APIService() }

    private val sensorRepository: SensorRepository by lazy {
        SensorRepositoryImpl(sensorService, Dispatchers.IO)
    }

    private val reportRepository: ReportRepository by lazy {
        ReportRepositoryImpl(apiService, json, Dispatchers.IO)
    }

    val discoverSensorsUseCase: DiscoverSensorsUseCase by lazy {
        DiscoverSensorsUseCase(sensorRepository)
    }

    val connectToSensorUseCase: ConnectToSensorUseCase by lazy {
        ConnectToSensorUseCase(sensorRepository)
    }

    private val streamHeartRateUseCase: StreamHeartRateUseCase by lazy {
        StreamHeartRateUseCase(sensorRepository)
    }

    private val reportHeartRateBatchUseCase: ReportHeartRateBatchUseCase by lazy {
        ReportHeartRateBatchUseCase(reportRepository)
    }

    val heartRateCollectionService: HeartRateCollectionService by lazy {
        HeartRateCollectionServiceImpl(
            streamHeartRate = streamHeartRateUseCase,
            reportHeartRateBatch = reportHeartRateBatchUseCase,
            coroutineContext = Dispatchers.IO,
        )
    }
}