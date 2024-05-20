package example.hotaku.eyecareapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import example.hotaku.timer.service.OnTimerViewModelCallback
import example.hotaku.timer.service.TimerService
import example.hotaku.timer.use_case.TimerServiceUseCase
import example.hotaku.timer.utils.TimeUtils.toTimeFormat
import example.hotaku.timer.utils.TimerUtils.BREAK_TIMER_MILLISECODS
import example.hotaku.timer.utils.TimerUtils.CONTINUE_TIMER_MILLISECONDS

class TimerScreenViewModel(
    private val timerServiceUseCase: TimerServiceUseCase = TimerServiceUseCase()
): ViewModel() {

    private lateinit var serviceIntent: Intent
    private var onTimerViewModelCallback: OnTimerViewModelCallback? = null

    var state by mutableStateOf(TimerScreenState())
        private set

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.StartService -> startService(event.context)
            is TimerScreenEvent.StartTimer -> startTimer(event.context)
            is TimerScreenEvent.StopTimer -> stopTimer()
        }
    }

    private fun startService(context: Context) {
        serviceIntent = Intent(context, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else  context.startService(serviceIntent)
        subscribeToTimerServiceCallBack()
        onTimerViewModelCallback?.let {
            TimerService.addListener(it)
        }
    }

    private fun subscribeToTimerServiceCallBack() {
        onTimerViewModelCallback = object : OnTimerViewModelCallback {
            override fun onBreakTimer(isBreak: Boolean) {
                state = state.copy(
                    isTimerStarted = true,
                    isBreak = isBreak
                )
            }

            override fun onTick(tick: Long) {
                val value = tick.toFloat() / if(state.isBreak) BREAK_TIMER_MILLISECODS else CONTINUE_TIMER_MILLISECONDS
                state = state.copy(
                    progress = 1 - value,
                    time = tick.toTimeFormat()
                )
            }

            override fun onStop(isKilled: Boolean) {
                state = state.copy(
                    isServiceStarted = !isKilled,
                    isTimerStarted = false,
                    isBreak = false,
                    progress = .0f,
                    time = "00:00",
                )
            }
        }
    }

    private fun startTimer(context: Context) {
        state.isTimerStarted.not().let {
            startService(context = context)
        }
        timerServiceUseCase.startTimer()
    }

    private fun stopTimer() {
        timerServiceUseCase.stopTimer()
    }


}
