package example.hotaku.timer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import example.hotaku.timer.repository.ServiceRepository
import example.hotaku.timer.repository.ServiceRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {

    @Provides
    @Singleton
    fun providesServiceRepository(
        @ApplicationContext context: Context
    ): ServiceRepository = ServiceRepositoryImpl(context)

}