package com.cardahealth.interviewapp.data.service

import com.cardahealth.interviewapp.data.repository.BATTERY_BYTE_INDEX
import com.cardahealth.interviewapp.domain.service.HeartRateCollectionService
import com.cardahealth.interviewapp.domain.service.HeartRateCollectionState
import com.cardahealth.interviewapp.domain.usecase.ReportHeartRateBatchUseCase
import com.cardahealth.interviewapp.domain.usecase.StreamHeartRateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val BATCH_SIZE = 10

class HeartRateCollectionServiceImpl(
    private val streamHeartRate: StreamHeartRateUseCase,
    private val reportHeartRateBatch: ReportHeartRateBatchUseCase,
    coroutineContext: CoroutineContext,
) : HeartRateCollectionService {

    private val scope = CoroutineScope(SupervisorJob() + coroutineContext)
    private val _state = MutableStateFlow(HeartRateCollectionState())
    override val state: StateFlow<HeartRateCollectionState> = _state.asStateFlow()

    private var job: Job? = null

    override fun start(sensorId: String) {
        if (job?.isActive == true) return
        job = scope.launch {
            _state.value = _state.value.copy(isRunning = true, errorMessage = null)
            val buffer = ArrayDeque<Int>(BATCH_SIZE)
            streamHeartRate(sensorId)
                .onEach { hr ->
                    val value = hr.get(BATTERY_BYTE_INDEX).toInt() and 0xFF
                    buffer.addLast(value)
                    _state.value = _state.value.copy(lastHeartRate = value)
                    if (buffer.size >= BATCH_SIZE) {
                        val toSend = buffer.toList()
                        buffer.clear()
                        sendBatch(toSend)
                    }
                }
                .catch { throwable ->
                    _state.value = _state.value.copy(
                        isRunning = false,
                        errorMessage = throwable.message,
                    )
                }
                .collect()
            _state.value = _state.value.copy(isRunning = false)
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
        _state.value = _state.value.copy(isRunning = false)
    }

    private suspend fun sendBatch(values: List<Int>) {
        runCatching { reportHeartRateBatch(values) }
            .onSuccess {
                _state.value = _state.value.copy(
                    batchesSent = _state.value.batchesSent + 1,
                )
            }
            .onFailure { throwable ->
                _state.value = _state.value.copy(errorMessage = throwable.message)
            }
    }
}