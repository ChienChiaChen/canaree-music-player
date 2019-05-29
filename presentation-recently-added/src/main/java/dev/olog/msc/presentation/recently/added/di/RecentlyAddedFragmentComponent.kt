package dev.olog.msc.presentation.recently.added.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment

@Component(
    modules = [RecentlyAddedFragmentModule::class],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface RecentlyAddedFragmentComponent : InjectionHelper<RecentlyAddedFragment> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance fragment: RecentlyAddedFragment, component: CoreComponent): RecentlyAddedFragmentComponent
    }

}
