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
import example.hotaku.timer.utils.TimerUtils.cancelTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerService : Service() {

    private var _timeer = MutableSharedFlow<Pair<Long?, Boolean>>()
    val timeer = _timeer.asSharedFlow()

    private var _isRun = MutableSharedFlow<Boolean>()
    val isRun = _isRun.asSharedFlow()

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
        startForegroundOwnService()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = localBinder

    private fun startForegroundOwnService() {
        val notification = timerNotificationManager.getTimerNotification(
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isSilent = true
        )
        try {
            ServiceCompat.startForeground(
                this,
                TimerNotificationManager.NOTIFICATION_REQUEST_CODE,
                notification,
                serviceType
            )
            scope.launch { _isRun.emit(true) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun continueTimer() {
        notify(
            title = getString(R.string.service_notification_service_timer_is_running),
            content = getString(R.string.service_notification_service_timer_continue),
            isTimerRun = true,
            isSilent = false
        )
        TimerUtils.continueTimer(
            onTick = { tick ->
                notify(
                    title = getString(R.string.service_notification_service_timer_is_running),
                    content = tick.toTimeFormat(),
                    isTimerRun = true
                )
                emitTimerValues(tick = tick, isBreak = false)
            },
            onFinish = { breakTimer() }
        )
    }

    private fun breakTimer() {
        notify(
            title = getString(R.string.service_notification_service_timer_is_running),
            content = getString(R.string.service_notification_service_timer_break),
            isTimerRun = true,
            isSilent = false
        )
        TimerUtils.breakTimer(
            onTick = { tick ->
                notify(
                    title = getString(R.string.service_notification_service_timer_is_running),
                    content = tick.toTimeFormat(),
                    isTimerRun = true
                )
                emitTimerValues(tick = tick, isBreak = true)
            },
            onFinish = { continueTimer() }
        )
    }

    private fun emitTimerValues(tick: Long, isBreak: Boolean) {
        scope.launch { _timeer.emit(tick to isBreak) }
    }


    private fun notify(
        title: String,
        content: String,
        isTimerRun: Boolean = false,
        isSilent: Boolean = true
    ) {
        val notification = timerNotificationManager.getTimerNotification(
            title = title,
            content = content,
            isTimerRun = isTimerRun,
            isSilent = isSilent
        )
        notificationManager.notify(TimerNotificationManager.NOTIFICATION_REQUEST_CODE, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Toast.makeText(this, getString(R.string.service_toast_service_stopped), Toast.LENGTH_SHORT).show()
    }

    fun startTimer() = continueTimer()

    fun stopTimer() {
        cancelTimer()
        scope.launch { _timeer.emit(null to false) }
        notify(
            title = getString(R.string.service_notification_service_ready),
            content = getString(R.string.service_notification_you_can_start_timer),
            isSilent = true,
        )
    }

    fun killService() {
        scope.launch { _isRun.emit(false) }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

}