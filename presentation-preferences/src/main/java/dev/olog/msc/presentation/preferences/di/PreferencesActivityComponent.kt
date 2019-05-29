package dev.olog.msc.presentation.preferences.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.pro.ProModule

@Component(
    modules = [
        ProModule::class,
        PreferenceActivityModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface PreferencesActivityComponent : InjectionHelper<PreferencesActivity> {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance instance: PreferencesActivity,
            component: CoreComponent
        ): PreferencesActivityComponent
    }

}
