package example.hotaku.eyecareapp.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.hotaku.eyecareapp.presentation.utils.millisToProgressValue
import example.hotaku.timer.repository.ServiceRepository
import example.hotaku.timer.service.TimerService
import example.hotaku.timer.utils.TimeUtils.toTimeFormat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
): ViewModel() {

    private var service: TimerService.LocalBinder? = null
    private lateinit var timerValue: SharedFlow<Pair<Long?, Boolean>>

    var state by mutableStateOf(TimerScreenState())
        private set

    private var _stopServiceChannel = Channel<Boolean>()
    val stopServiceChannel = _stopServiceChannel.receiveAsFlow()


    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.StartService -> startService()
            is TimerScreenEvent.StartTimer -> startTimer()
            is TimerScreenEvent.StopTimer -> stopTimer()
            is TimerScreenEvent.UnbindService -> unBindService()
        }
    }

    private fun startService() {
        viewModelScope.launch {
            serviceRepository.bindService().collect { serviceBinder ->
                service = serviceBinder
                service?.getService()?.timeer?.let {
                    timerValue = it
                    consumeChannelData()
                    collectTimeData()
                }
            }
        }
    }

    private fun consumeChannelData() {
        viewModelScope.launch {
            service?.getService()?.isRun?.collect { isRun ->
                _stopServiceChannel.send(isRun)
                state = TimerScreenState()
            }
        }
    }

    private fun collectTimeData() {
        viewModelScope.launch {
            timerValue.collect { timerValue ->
                if (timerValue.first == null) {
                    state = TimerScreenState()
                } else {
                    val value = millisToProgressValue(timerValue)
                    state = state.copy(
                        isTimerStarted = true,
                        isBreak = timerValue.second,
                        progress = 1 - value,
                        time = timerValue.first!!.toTimeFormat()
                    )
                }
            }
        }
    }

    private fun startTimer() {
        startService()
        service?.getService()?.startTimer()
    }

    private fun stopTimer() {
        service?.getService()?.stopTimer()
        state = TimerScreenState()
    }

    private fun unBindService() = serviceRepository.unbindService()

}
