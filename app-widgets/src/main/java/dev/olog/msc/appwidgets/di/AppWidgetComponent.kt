package dev.olog.msc.appwidgets.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.appwidgets.base.WidgetColored
import javax.inject.Scope

@Scope
annotation class PerWidget

@Component(dependencies = [CoreComponent::class])
@PerWidget
interface AppWidgetComponent : InjectionHelper<WidgetColored> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): AppWidgetComponent
    }

}