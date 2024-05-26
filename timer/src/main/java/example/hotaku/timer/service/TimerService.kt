package example.hotaku.timer.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import example.hotaku.timer.R
import example.hotaku.timer.notification.TimerNotificationManager
import example.hotaku.timer.utils.TimeUtils.toTimeFormat
import example.hotaku.timer.utils.TimerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerService : Service() {

    private var _timeer = MutableSharedFlow<Pair<Long, Boolean>>()
    val timeer = _timeer

    private var localBinder = LocalBinder()

    private val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
    private lateinit var timerNotificationManager: TimerNotificationManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var scope: CoroutineScope


    inner class LocalBinder : Binder() {
        fun getService() = this@TimerService
    }

    override fun onCreate() {
        timerNotificationManager = TimerNotificationManager(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        startForegroundOwnService()
        return localBinder
    }

    private fun startForegroundOwnService() {
        val notification = timerNotificationManager.createTimerNotification(
            context = this,
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isRun = false
        )
        try {
            ServiceCompat.startForeground(
                this,
                TimerNotificationManager.NOTIFICATION_REQUEST_CODE,
                notification,
                serviceType
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun continueTimer() {
        TimerUtils.continueTimer(
            onTick = { tick ->
                val notification = timerNotificationManager.createTimerNotification(
                    context = this,
                    title = getString(R.string.service_notification_service_timer_is_running),
                    content = tick.toTimeFormat(),
                    isRun = true
                )
                scope.launch {
                    _timeer.emit(tick to false)
                }
                notificationManager.notify(
                    TimerNotificationManager.NOTIFICATION_REQUEST_CODE,
                    notification
                )
            },
            onFinish = {
                breakTimer()
            }
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
                scope.launch {
                    _timeer.emit(tick to true)
                }
                notificationManager.notify(
                    TimerNotificationManager.NOTIFICATION_REQUEST_CODE,
                    notification
                )
            },
            onFinish = { continueTimer() }
        )
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
    }

    fun startTimer() = continueTimer()

    fun stopTimer() {
        scope.cancel()
        TimerUtils.cancelTimer()
        val notification = timerNotificationManager.createTimerNotification(
            context = this,
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isRun = false
        )
        notificationManager.notify(TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification)
    }

    fun killService() {
        scope.cancel()
        stopSelf()
    }

}