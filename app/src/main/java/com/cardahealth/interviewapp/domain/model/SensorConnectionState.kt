package com.cardahealth.interviewapp.domain.model

import androidx.annotation.StringRes
import com.cardahealth.interviewapp.R

enum class SensorConnectionState(val state: Int, @StringRes val value: Int) {
    Disconnected(0, R.string.sensor_state_disconnected),
    Connecting(1, R.string.sensor_state_connecting),
    Connected(2, R.string.sensor_state_connected);

    companion object {
        fun fromRaw(raw: Int): SensorConnectionState =
            entries.firstOrNull { it.state == raw }
                ?: error("Unknown sensor connection state: $raw")
    }
}