package com.cardahealth.interviewapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cardahealth.interviewapp.di.ServiceLocator

class HomeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return HomeViewModel(
            discoverSensors = ServiceLocator.discoverSensorsUseCase,
            connectToSensor = ServiceLocator.connectToSensorUseCase,
            heartRateCollectionService = ServiceLocator.heartRateCollectionService,
        ) as T
    }
}