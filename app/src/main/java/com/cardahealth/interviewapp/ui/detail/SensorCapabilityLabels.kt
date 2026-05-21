package com.cardahealth.interviewapp.ui.detail

import androidx.annotation.StringRes
import com.cardahealth.interviewapp.R
import com.cardahealth.interviewapp.domain.model.SensorCapability

@StringRes
fun SensorCapability.labelRes(): Int = when (this) {
    SensorCapability.HeartRate -> R.string.capability_heart_rate
    SensorCapability.Spo2 -> R.string.capability_spo2
}
