package ir.hoseinsa.eyecareapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ir.hoseinsa.eyecareapp.utils.OnTimerServiceCallback
import ir.hoseinsa.eyecareapp.utils.TimerService
import ir.hoseinsa.eyecareapp.utils.toTimeFormat

class TimerScreenViewModel: ViewModel() {

    private lateinit var serviceIntent: Intent
    private var onTimerServiceCallback: OnTimerServiceCallback? = null

    var state by mutableStateOf(TimerScreenState())
        private set

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.startTimer -> startTimer(event.context)
            is TimerScreenEvent.stopTimer -> stopTimer(event.context)
        }
    }

    private fun startTimer(context: Context) {
        serviceIntent = Intent(context, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else  context.startService(serviceIntent)
        onTimerServiceCallback?.let {
            TimerService().addListener(it)
        }
        subscribeToTimerServiceCallBack()
    }

    private fun subscribeToTimerServiceCallBack() {
        state = state.copy(
            isStarted = true
        )
        onTimerServiceCallback = object : OnTimerServiceCallback {
            override fun onBreakTimer(isBreak: Boolean) {
                state = state.copy(
                    isBreak = isBreak
                )
            }

            override fun onTick(tick: Long) {
                state = state.copy(
                    time = tick.toTimeFormat()
                )
            }

        }
    }

    private fun stopTimer(context: Context) {
        context.stopService(serviceIntent)
        state = TimerScreenState()
    }


}
