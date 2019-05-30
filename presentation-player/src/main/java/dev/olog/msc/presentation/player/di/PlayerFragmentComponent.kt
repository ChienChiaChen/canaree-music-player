package dev.olog.msc.presentation.player.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.player.PlayerFragment

@Component(
    modules = [
        PlayerFragmentModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface PlayerFragmentComponent : InjectionHelper<PlayerFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): PlayerFragmentComponent
    }

}