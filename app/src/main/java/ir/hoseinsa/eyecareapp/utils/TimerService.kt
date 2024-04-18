package ir.hoseinsa.eyecareapp.utils

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ServiceCompat
import ir.hoseinsa.eyecareapp.ui.main.TimerScreenViewModel
import ir.hoseinsa.eyecareapp.utils.NotificationUtils.notificationBuilder

class TimerService : Service(), OnServiceCallback {

    private var serviceId: Int = 0
    private val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0

    private val CONTINUE_TIMER_MILLISECONDS = 40_000L
    private val BREAK_TIMER_MILLISECODS = 20_000L

    companion object {

        private var onTimerServiceCallback: OnTimerServiceCallback? = null
        fun addListener(callback: OnTimerServiceCallback) {
            onTimerServiceCallback = callback
        }

    }

    override fun onCreate() {
        TimerScreenViewModel.addListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceId = startId
        val notification = this.notificationBuilder(
            title = "title",
            content = "title"
        )
        try {
            ServiceCompat.startForeground(this, serviceId, notification, serviceType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null


    private fun continueTimer() {
        TimerUtils.getInstance(
            millisInFuture = CONTINUE_TIMER_MILLISECONDS,
            onTick = { tick ->
                onTimerServiceCallback?.let {
                    it.onBreakTimer(false)
                    it.onTick(tick)
                }
            },
            onFinish = {
                breakTimer()
            }
        ).start()
    }

    private fun breakTimer() {
        TimerUtils.getInstance(
            millisInFuture = BREAK_TIMER_MILLISECODS,
            onTick = { tick ->
                onTimerServiceCallback?.let {
                    it.onBreakTimer(true)
                    it.onTick(tick)
                }
            },
            onFinish = {
                continueTimer()
            }
        ).start()
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Toast.makeText(this, "ServiceS topped", Toast.LENGTH_SHORT).show()
    }

    override fun startTimer() = continueTimer()

    override fun stopTimer() = TimerUtils.stopTimer()

}