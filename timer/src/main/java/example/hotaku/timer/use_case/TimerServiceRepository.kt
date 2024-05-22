package example.hotaku.timer.use_case

import example.hotaku.timer.service.ServiceCallback
import javax.inject.Inject

class TimerServiceRepository @Inject constructor() {

    fun startTimer() = serviceCallback?.startTimer()

    fun stopTimer() = serviceCallback?.stopTimer()

    companion object {

        private var serviceCallback: ServiceCallback? = null

        fun addListener(callBack: ServiceCallback) {
            serviceCallback = callBack
        }

    }


}