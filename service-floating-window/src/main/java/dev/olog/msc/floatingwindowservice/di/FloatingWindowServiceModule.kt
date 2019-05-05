package dev.olog.msc.floatingwindowservice.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.qualifier.ServiceContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.floatingwindowservice.FloatingWindowService

@Module
class FloatingWindowServiceModule(
        private val service: FloatingWindowService
) {

    @Provides
    @ServiceLifecycle
    internal fun provideLifecycle(): Lifecycle = service.lifecycle

    @Provides
    @ServiceContext
    internal fun provideContext(): Context = service

    @Provides
    internal fun provideService() : Service = service

    @Provides
    internal fun provideNotificationManager(): NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}