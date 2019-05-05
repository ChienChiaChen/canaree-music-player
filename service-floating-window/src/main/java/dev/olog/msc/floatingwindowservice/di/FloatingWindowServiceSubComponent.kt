package dev.olog.msc.floatingwindowservice.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.floatingwindowservice.FloatingWindowService

@Subcomponent(modules = arrayOf(
        FloatingWindowServiceModule::class
))
@PerService
interface FloatingWindowServiceSubComponent : AndroidInjector<FloatingWindowService> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FloatingWindowService>() {

        abstract fun module(module: FloatingWindowServiceModule): Builder

        override fun seedInstance(instance: FloatingWindowService) {
            module(FloatingWindowServiceModule(instance))
        }
    }

}