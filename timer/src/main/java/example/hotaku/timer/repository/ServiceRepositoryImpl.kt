package example.hotaku.timer.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import example.hotaku.timer.service.TimerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context): ServiceRepository {

    private lateinit var serviceIntent: Intent
    private lateinit var service: TimerService.LocalBinder

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@ServiceRepositoryImpl.service = service as TimerService.LocalBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {}

    }

    override fun bindService(): Flow<TimerService.LocalBinder?> = flow {
        serviceIntent = Intent(context, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else context.startService(serviceIntent)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        emit(service)
    }.catch {
        it.printStackTrace()
        Log.e(::bindService.name, "startService: ${it.message}")
    }

    override fun unbindService() {
        context.unbindService(serviceConnection)
    }

}