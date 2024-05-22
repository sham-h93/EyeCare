package example.hotaku.eyecareapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import example.hotaku.timer.use_case.TimerServiceUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimerServiceUseCase(): TimerServiceUseCase = TimerServiceUseCase()

}