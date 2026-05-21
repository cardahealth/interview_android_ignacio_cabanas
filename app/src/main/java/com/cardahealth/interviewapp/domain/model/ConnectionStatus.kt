package com.cardahealth.interviewapp.domain.model

sealed interface ConnectionStatus {
    data object Disconnected : ConnectionStatus
    data object Connecting : ConnectionStatus
    data object Connected : ConnectionStatus
    data class Error(val message: String) : ConnectionStatus
}
