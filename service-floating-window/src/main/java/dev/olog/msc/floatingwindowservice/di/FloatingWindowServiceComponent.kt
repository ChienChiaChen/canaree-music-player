package dev.olog.msc.floatingwindowservice.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.floatingwindowservice.FloatingWindowService

@Component(
    modules = [
        FloatingWindowServiceModule::class
    ], dependencies = [CoreComponent::class]
)
@PerService
interface FloatingWindowServiceComponent : InjectionHelper<FloatingWindowService> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance fragment: FloatingWindowService, component: CoreComponent): FloatingWindowServiceComponent
    }

}