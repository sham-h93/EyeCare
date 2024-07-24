package com.hotaku.common

import android.os.CountDownTimer

object TimerUtils {

    private var timer: CountDownTimer? = null
    private const val MILLIS_INTERVAL = 1000L
    const val CONTINUE_TIMER_MILLISECONDS = 1_200_000L
    const val BREAK_TIMER_MILLISECODS = 20_000L

    private fun getInstance(
        millisInFuture: Long,
        onTick: (tick: Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer = object : CountDownTimer(millisInFuture, MILLIS_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            onTick(millisUntilFinished)
        }

        override fun onFinish() {
            timer = null
            onFinish()
        }
    }

    fun continueTimer(
        onTick: (tick: Long) -> Unit,
        onFinish: () -> Unit
    ) {
        if (timer == null) {
            timer = getInstance(
                millisInFuture = CONTINUE_TIMER_MILLISECONDS,
                onTick =  onTick,
                onFinish = onFinish
            )
        }
        timer?.start()
    }

    fun breakTimer(
        onTick: (tick: Long) -> Unit,
        onFinish: () -> Unit
    ) {
        if (timer == null) {
            timer = getInstance(
                millisInFuture = BREAK_TIMER_MILLISECODS,
                onTick = onTick,
                onFinish = onFinish
            )
        }
        timer?.start()
    }

    fun cancelTimer() {
        timer?.cancel()
    }

}