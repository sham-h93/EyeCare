package example.hotaku.eyecareapp

import android.app.Application
import example.hotaku.eyecareapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EyeCareApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@EyeCareApp)
            modules(appModule)
        }
    }

}