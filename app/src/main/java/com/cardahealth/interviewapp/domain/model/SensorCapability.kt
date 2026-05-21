package com.cardahealth.interviewapp.domain.model

enum class SensorCapability(val raw: String) {
    HeartRate("heart_rate"),
    Spo2("spo2");

    companion object {
        fun fromRaw(raw: String): SensorCapability? =
            entries.firstOrNull { it.raw == raw }
    }
}