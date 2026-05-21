package com.cardahealth.interviewapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HeartRateBatchDto(
    val values: List<Int>,
)