package dev.olog.msc.floatingwindowservice.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.*
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.qualifier.ServiceContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.floatingwindowservice.FloatingWindowService


@Module(subcomponents = [FloatingWindowServiceSubComponent::class])
abstract class FloatingWindowServiceInjector {

    @Binds
    @IntoMap
    @ClassKey(FloatingWindowService::class)
    internal abstract fun injectorFactory(builder: FloatingWindowServiceSubComponent.Factory): AndroidInjector.Factory<*>

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

@Subcomponent(modules = [FloatingWindowServiceModule::class])
@PerService
interface FloatingWindowServiceSubComponent : AndroidInjector<FloatingWindowService> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<FloatingWindowService> {
        override fun create(@BindsInstance instance: FloatingWindowService): FloatingWindowServiceSubComponent
    }
}