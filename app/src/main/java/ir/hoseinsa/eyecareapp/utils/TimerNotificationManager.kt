package ir.hoseinsa.eyecareapp.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import ir.hoseinsa.eyecareapp.R

@SuppressLint("UnspecifiedRegisterReceiverFlag")
class TimerNotificationManager(service: TimerService): BroadcastReceiver() {

    private var startTimerIntent: PendingIntent? = null
    private var stopTimerIntent: PendingIntent? = null
    private var stopServiceIntent: PendingIntent? = null


    override fun onReceive(context: Context?, intent: Intent?) {

    }


    init {

        startTimerIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_START_TIMER).setPackage(service.packageName),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stopTimerIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP_TIMER).setPackage(service.packageName),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stopServiceIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP_SERVICE).setPackage(service.packageName),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

    fun createTimerNotification(
        context: Context,
        title: String,
        content: String
    ): Notification {
        createNotificationChannel(context)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .addAction(R.drawable.all_play, "Start", startTimerIntent)
            .addAction(R.drawable.all_pause, "Pause", stopTimerIntent)
            .addAction(R.drawable.all_close, "Stop", stopServiceIntent)
            .setSmallIcon(R.drawable.all_visible)
            .setContentTitle(title)
            .setContentText(content)
            .build()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MAX
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {

        const val CHANNEL_ID = "202020app"
        const val CHANNEL_NAME = "20-20-20 Service notification"
        const val NOTIFICATION_REQUEST_CODE = 132

        const val ACTION_START_TIMER = "startTimer"
        const val ACTION_STOP_TIMER = "stopTimer"
        const val ACTION_STOP_SERVICE = "stopService"

    }

}