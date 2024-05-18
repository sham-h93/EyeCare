package example.hotaku.timer.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ServiceCompat
import example.hotaku.timer.R
import example.hotaku.timer.notification.TimerNotificationManager
import example.hotaku.timer.use_case.TimerServiceUseCase
import example.hotaku.timer.utils.TimeUtils.toTimeFormat
import example.hotaku.timer.utils.TimerUtils

class TimerService : Service(), OnServiceCallback {

    private var serviceId: Int = 0
    private val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
    private lateinit var timerNotificationManager: TimerNotificationManager
    private lateinit var notificationManager: NotificationManager

    companion object {

        private var onTimerViewModelCallback: OnTimerViewModelCallback? = null
        fun addListener(callback: OnTimerViewModelCallback) {
            onTimerViewModelCallback = callback
        }

    }

    override fun onCreate() {
        TimerServiceUseCase.addListener(this)
        timerNotificationManager = TimerNotificationManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceId = startId
        val notification = timerNotificationManager.createTimerNotification(
            context = this,
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isRun = false
        )
        try {
            ServiceCompat.startForeground(this, TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification, serviceType)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null


    private fun continueTimer() {

        TimerUtils.continueTimer(
            onTick = { tick ->
                val notification = timerNotificationManager.createTimerNotification(
                    context = this,
                    title = getString(R.string.service_notification_service_timer_is_running),
                    content = tick.toTimeFormat(),
                    isRun = true
                )
                notificationManager.notify(TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification)
                onTimerViewModelCallback?.let {
                    it.onBreakTimer(false)
                    it.onTick(tick)
                }
            },
            onFinish = { breakTimer() }
        )
    }

    private fun breakTimer() {
        TimerUtils.breakTimer(
            onTick = { tick ->
                val notification = timerNotificationManager.createTimerNotification(
                    context = this,
                    title = getString(R.string.service_notification_service_timer_is_running),
                    content = tick.toTimeFormat(),
                    isRun = true
                )
                notificationManager.notify(TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification)
                onTimerViewModelCallback?.let {
                    it.onBreakTimer(true)
                    it.onTick(tick)
                }
            },
            onFinish = { continueTimer() }
        )
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Toast.makeText(this, "ServiceS topped", Toast.LENGTH_SHORT).show()
    }

    override fun startTimer() = continueTimer()

    override fun stopTimer() {
        TimerUtils.cancelTimer()
        val notification = timerNotificationManager.createTimerNotification(
            context = this,
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isRun = false
        )
        notificationManager.notify(TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification)
        onTimerViewModelCallback?.onStop()
    }

}