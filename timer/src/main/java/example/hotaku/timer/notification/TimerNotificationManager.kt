package example.hotaku.timer.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import example.hotaku.timer.R
import example.hotaku.timer.service.TimerService


@SuppressLint("UnspecifiedRegisterReceiverFlag")
class TimerNotificationManager(private val service: TimerService): BroadcastReceiver() {

    private var startTimerIntent: PendingIntent? = null
    private var stopTimerIntent: PendingIntent? = null
    private var stopServiceIntent: PendingIntent? = null

    private val notificationSound: Uri by lazy { Uri.parse("android.resource://" + service.packageName + "/" + R.raw.notification) }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when(intent.action) {
                ACTION_START_TIMER -> service.startTimer()
                ACTION_STOP_TIMER -> service.stopTimer()
                ACTION_STOP_SERVICE -> {
                    service.run {
                        stopTimer()
                        killService()
                        unregisterReceiver(this@TimerNotificationManager)
                    }
                }
            }
        }
    }


    init {

        createNotificationChannel(service)

        startTimerIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_START_TIMER).setPackage(service.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        stopTimerIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP_TIMER).setPackage(service.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        stopServiceIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP_SERVICE).setPackage(service.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val filters = IntentFilter().apply {
            addAction(ACTION_START_TIMER)
            addAction(ACTION_STOP_TIMER)
            addAction(ACTION_STOP_SERVICE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            service.registerReceiver(this, filters, Context.RECEIVER_NOT_EXPORTED)
        } else  service.registerReceiver(this, filters)
    }

    fun buildTimerNotification(
        context: Context,
        title: String,
        content: String,
        isTimerRun: Boolean,
        isSilent: Boolean
    ): Notification {

        var smallIcon = R.drawable.all_visible
        var actionIcon = R.drawable.all_play
        var actionText = context.getString(R.string.service_notification_start_timer)
        var actionIntent = startTimerIntent
        
        if(isTimerRun) {
            actionIcon = R.drawable.all_pause
            actionText = context.getString(R.string.service_notification_stop_timer)
            actionIntent = stopTimerIntent
        } else {
            smallIcon = R.drawable.all_invisible
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .addAction(actionIcon, actionText, actionIntent)
            .addAction(R.drawable.all_close, context.getString(R.string.service_notification_stop_service), stopServiceIntent)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // this value ignored in api 26 above so we set it in NotificationChannel
            .setSound(notificationSound, AudioManager.STREAM_NOTIFICATION) // this value ignored in api 26 above so we set it in NotificationChannel
            .setSilent(isSilent)
            .setContentText(content)
            .build()
    }

    fun createNotificationChannel(context: Context) {
        val audioAttributes = buildAudioAttributes()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                setSound(notificationSound, audioAttributes)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun buildAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()

    fun getTimerNotification(
        title: String,
        content: String,
        isTimerRun: Boolean = false,
        isSilent: Boolean
    ): Notification = buildTimerNotification(
        context = service,
        title = title,
        content = content,
        isTimerRun = isTimerRun,
        isSilent = isSilent
    )

    companion object {

        const val CHANNEL_ID = "202020app"
        const val CHANNEL_NAME = "20-20-20 Service notification"
        const val NOTIFICATION_REQUEST_CODE = 100

        const val ACTION_START_TIMER = "startTimer"
        const val ACTION_STOP_TIMER = "stopTimer"
        const val ACTION_STOP_SERVICE = "stopService"

    }

}