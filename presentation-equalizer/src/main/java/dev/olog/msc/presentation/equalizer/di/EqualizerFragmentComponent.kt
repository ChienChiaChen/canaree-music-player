package dev.olog.msc.presentation.equalizer.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.equalizer.EqualizerFragment

@Component(
    modules = [
        ViewModelModule::class,
        EqualizerFragmentModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface EqualizerFragmentComponent : InjectionHelper<EqualizerFragment> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): EqualizerFragmentComponent
    }

}
