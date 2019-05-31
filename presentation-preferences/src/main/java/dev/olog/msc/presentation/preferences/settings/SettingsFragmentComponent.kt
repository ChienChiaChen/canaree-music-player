package dev.olog.msc.presentation.preferences.settings

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerFragment

@Component(dependencies = [CoreComponent::class])
@PerFragment
interface SettingsFragmentComponent : InjectionHelper<SettingsFragment> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): SettingsFragmentComponent
    }

}
