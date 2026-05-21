package com.cardahealth.interviewapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cardahealth.interviewapp.di.ServiceLocator

class SensorDetailViewModelFactory(
    private val sensorId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SensorDetailViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return SensorDetailViewModel(
            sensorId = sensorId,
            apiRepo = ServiceLocator.apiServiceRepository,
            getAssignedSensors = ServiceLocator.getAssignedSensorsUseCase,
            sensorRepo = ServiceLocator.sensorRepository,
            connectionManager = ServiceLocator.sensorConnectionManager,
        ) as T
    }
}
