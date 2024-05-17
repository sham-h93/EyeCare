package example.hotaku.eyecareapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import example.hotaku.timer.service.OnServiceCallback
import example.hotaku.timer.service.OnTimerViewModelCallback
import example.hotaku.timer.service.TimerService
import example.hotaku.timer.utils.TimeUtils.toTimeFormat

class TimerScreenViewModel: ViewModel() {

    private lateinit var serviceIntent: Intent
    private var onTimerViewModelCallback: OnTimerViewModelCallback? = null

    companion object {

        private var onServiceCallback: OnServiceCallback? = null

        fun addListener(callBack: OnServiceCallback) {
            onServiceCallback = callBack
        }

    }

    var state by mutableStateOf(TimerScreenState())
        private set

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.StartService -> startService(event.context)
            is TimerScreenEvent.StartTimer -> startTimer()
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
                Log.d("subscribeToTimerServiceCallBack", "onBreakTimer: $isBreak")
                state = state.copy(
                    isStarted = true,
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

    private fun startTimer() {
        onServiceCallback?.startTimer()
    }

    private fun stopTimer() {
        onServiceCallback?.stopTimer()
//        context.stopService(serviceIntent)
        state = TimerScreenState()
    }


}
