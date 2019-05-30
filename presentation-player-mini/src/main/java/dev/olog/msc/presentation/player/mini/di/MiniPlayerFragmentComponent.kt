package dev.olog.msc.presentation.player.mini.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragment

@Component(
    modules = [
        MiniPlayerFragmentModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface MiniPlayerFragmentComponent : InjectionHelper<MiniPlayerFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): MiniPlayerFragmentComponent
    }

}