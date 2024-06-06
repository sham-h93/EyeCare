package example.hotaku.timer.repository

import example.hotaku.timer.service.TimerService
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {

    fun bindService(): Flow<TimerService.LocalBinder?>

    fun unbindService()


}