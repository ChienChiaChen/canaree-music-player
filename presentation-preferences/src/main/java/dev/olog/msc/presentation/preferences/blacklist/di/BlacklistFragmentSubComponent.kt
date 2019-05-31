package dev.olog.msc.presentation.preferences.blacklist.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment

@Component(
    modules = [
        ViewModelModule::class,
        BlacklistFragmentModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface BlacklistFragmentSubComponent : InjectionHelper<BlacklistFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): BlacklistFragmentSubComponent
    }

}