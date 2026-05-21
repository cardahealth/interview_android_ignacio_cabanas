package com.cardahealth.interviewapp.ui.detail

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ASSIGNMENT_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US)
        .withZone(ZoneId.systemDefault())

internal fun formatAssignmentDate(epochSeconds: Long): String =
    ASSIGNMENT_DATE_FORMATTER.format(Instant.ofEpochSecond(epochSeconds))
