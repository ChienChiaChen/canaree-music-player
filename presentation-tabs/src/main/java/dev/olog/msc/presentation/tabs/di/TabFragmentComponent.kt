package dev.olog.msc.presentation.tabs.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.tabs.TabFragment

@Component(
    modules = [TabFragmentModule::class],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface TabFragmentComponent : InjectionHelper<TabFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): TabFragmentComponent
    }

}