package dev.olog.msc.dagger

import dagger.Component
import dev.olog.msc.app.App
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper

@Component(dependencies = [CoreComponent::class])
@PerApp
interface AppComponent : InjectionHelper<App> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): AppComponent

    }

}