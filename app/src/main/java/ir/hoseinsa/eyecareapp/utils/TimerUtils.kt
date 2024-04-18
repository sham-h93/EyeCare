package ir.hoseinsa.eyecareapp.utils

import android.os.CountDownTimer
import android.util.Log
import ir.hoseinsa.eyecareapp.utils.TimeUtils.toTimeFormat

object TimerUtils {

    private const val MILLIS_INTERVAL = 1000L
    private lateinit var timer: CountDownTimer

    fun getInstance(
        millisInFuture: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer {
        if (!::timer.isInitialized) {
            timer = object : CountDownTimer(millisInFuture, MILLIS_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    onTick(millisUntilFinished)
                    Log.e(::TimerService.name, "onTick: ${millisUntilFinished.toTimeFormat()}")
                }

                override fun onFinish() {
                    onFinish()
                }
            }
        }
        return timer
    }

    fun stopTimer() = timer.cancel()

}