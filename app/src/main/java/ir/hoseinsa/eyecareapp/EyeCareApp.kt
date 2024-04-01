package ir.hoseinsa.eyecareapp

import android.app.Application
import ir.hoseinsa.eyecareapp.di.appModule
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