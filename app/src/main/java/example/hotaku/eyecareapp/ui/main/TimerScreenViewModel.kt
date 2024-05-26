package example.hotaku.eyecareapp.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.hotaku.timer.service.TimerService
import example.hotaku.timer.utils.TimeUtils.toTimeFormat
import example.hotaku.timer.utils.TimerUtils.BREAK_TIMER_MILLISECODS
import example.hotaku.timer.utils.TimerUtils.CONTINUE_TIMER_MILLISECONDS
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(): ViewModel() {

    private lateinit var serviceIntent: Intent
    private var service: TimerService.LocalBinder? = null
    private lateinit var timerValue: SharedFlow<Pair<Long, Boolean>>

    var state by mutableStateOf(TimerScreenState())
        private set

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            state = state.copy(isServiceStarted = true)
            this@TimerScreenViewModel.service = service as TimerService.LocalBinder
            timerValue  = service.getService().timeer.asSharedFlow()
            collectTimeData()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            state = state.copy(isServiceStarted = false)
        }
    }

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.StartService -> startService(event.context)
            is TimerScreenEvent.StartTimer -> startTimer(event.context)
            is TimerScreenEvent.StopTimer -> stopTimer()
            is TimerScreenEvent.StopService -> stopService(event.context)
        }
    }

    private fun startService(context: Context) {
        serviceIntent = Intent(context, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else context.startService(serviceIntent)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun collectTimeData() {
        viewModelScope.launch {
            timerValue.collect {
                val value = it.first.toFloat() / if(state.isBreak) BREAK_TIMER_MILLISECODS else CONTINUE_TIMER_MILLISECONDS
                state = state.copy(
                    isTimerStarted = true,
                    isBreak = it.second,
                    progress = 1 - value,
                    time = it.first.toTimeFormat()
                )
            }
        }
    }

    private fun startTimer(context: Context) {
        if (!state.isServiceStarted) {
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        service?.getService()?.startTimer()
    }


    private fun stopTimer() {
        service?.getService()?.stopTimer()
        state = TimerScreenState()
    }

    private fun stopService(context: Context) {
        context.unbindService(serviceConnection)
    }

}
