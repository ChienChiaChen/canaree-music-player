package dev.olog.msc.floatingwindowservice.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.dagger.qualifier.ServiceContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.floatingwindowservice.FloatingWindowService

fun FloatingWindowService.inject() {
    DaggerFloatingWindowServiceComponent.factory()
        .create(this, coreComponent())
        .inject(this)
}


@Module
abstract class FloatingWindowServiceModule {
    @Binds
    @ServiceContext
    internal abstract fun provideContext(service: FloatingWindowService): Context

    @Binds
    internal abstract fun provideService(service: FloatingWindowService): Service

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideNotificationManager(service: FloatingWindowService): NotificationManager {
            return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        @JvmStatic
        @ServiceLifecycle
        internal fun provideLifecycle(service: FloatingWindowService): Lifecycle = service.lifecycle
    }
}