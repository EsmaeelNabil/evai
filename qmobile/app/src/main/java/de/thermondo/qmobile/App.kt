package de.thermondo.qmobile

import android.app.Application
import de.thermondo.qmobile.di.appModule
import de.thermondo.qmobile.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule, networkModule)
        }
    }
}