package dev.olog.msc.floatingwindowservice.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.floatingwindowservice.FloatingWindowService


@Module(subcomponents = arrayOf(FloatingWindowServiceSubComponent::class))
abstract class FloatingWindowServiceInjector {

    @Binds
    @IntoMap
    @ClassKey(FloatingWindowService::class)
    internal abstract fun injectorFactory(builder: FloatingWindowServiceSubComponent.Builder)
            : AndroidInjector.Factory<*>

}