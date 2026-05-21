package com.cardahealth.interviewapp.di

import android.util.Log
import com.cardahealth.apiservice.APIService
import com.cardahealth.interviewapp.data.repository.APIServiceRepositoryImpl
import com.cardahealth.interviewapp.data.repository.SensorRepositoryImpl
import com.cardahealth.interviewapp.data.service.HeartRateCollectionServiceImpl
import com.cardahealth.interviewapp.data.service.SensorConnectionManagerImpl
import com.cardahealth.interviewapp.domain.repository.APIServiceRepository
import com.cardahealth.interviewapp.domain.repository.SensorRepository
import com.cardahealth.interviewapp.domain.service.HeartRateCollectionService
import com.cardahealth.interviewapp.domain.service.SensorConnectionManager
import com.cardahealth.interviewapp.domain.usecase.ConnectToSensorUseCase
import com.cardahealth.interviewapp.domain.usecase.DiscoverSensorsUseCase
import com.cardahealth.interviewapp.domain.usecase.GetAssignedSensorsUseCase
import com.cardahealth.interviewapp.domain.usecase.ReportHeartRateBatchUseCase
import com.cardahealth.interviewapp.domain.usecase.StreamHeartRateUseCase
import com.cardahealth.sensorservice.SensorService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

private const val SENSOR_SERVICE_TAG = "SensorService"

object ServiceLocator {

    private val json: Json by lazy { Json { ignoreUnknownKeys = true } }

    private val sensorServiceErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(SENSOR_SERVICE_TAG, "Uncaught sensor service error", throwable)
    }

    private val sensorServiceScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO + sensorServiceErrorHandler)
    }

    private val sensorService: SensorService by lazy {
        SensorService(sensorServiceScope, dataErrorProbability = 100)
    }
    private val apiService: APIService by lazy { APIService() }

    val sensorRepository: SensorRepository by lazy {
        SensorRepositoryImpl(sensorService, Dispatchers.IO)
    }

    val apiServiceRepository: APIServiceRepository by lazy {
        APIServiceRepositoryImpl(apiService, json, Dispatchers.IO)
    }

    val discoverSensorsUseCase: DiscoverSensorsUseCase by lazy {
        DiscoverSensorsUseCase(sensorRepository)
    }

    val connectToSensorUseCase: ConnectToSensorUseCase by lazy {
        ConnectToSensorUseCase(sensorRepository)
    }

    val getAssignedSensorsUseCase: GetAssignedSensorsUseCase by lazy {
        GetAssignedSensorsUseCase(apiServiceRepository)
    }

    private val connectionScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    val sensorConnectionManager: SensorConnectionManager by lazy {
        SensorConnectionManagerImpl(connectToSensorUseCase, connectionScope)
    }

    private val streamHeartRateUseCase: StreamHeartRateUseCase by lazy {
        StreamHeartRateUseCase(sensorRepository)
    }

    private val reportHeartRateBatchUseCase: ReportHeartRateBatchUseCase by lazy {
        ReportHeartRateBatchUseCase(apiServiceRepository)
    }

    val heartRateCollectionService: HeartRateCollectionService by lazy {
        HeartRateCollectionServiceImpl(
            streamHeartRate = streamHeartRateUseCase,
            reportHeartRateBatch = reportHeartRateBatchUseCase,
            coroutineContext = Dispatchers.IO,
        )
    }
}