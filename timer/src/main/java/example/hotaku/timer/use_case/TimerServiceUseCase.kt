package example.hotaku.timer.use_case

import example.hotaku.timer.service.OnServiceCallback

class TimerServiceUseCase {

    fun startTimer() = onServiceCallback?.startTimer()

    fun stopTimer() = onServiceCallback?.stopTimer()

    companion object {

        private var onServiceCallback: OnServiceCallback? = null

        fun addListener(callBack: OnServiceCallback) {
            onServiceCallback = callBack
        }

    }


}