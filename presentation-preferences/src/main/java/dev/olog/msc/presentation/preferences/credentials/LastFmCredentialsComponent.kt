package dev.olog.msc.presentation.preferences.credentials

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.dagger.scope.PerFragment

fun LastFmCredentialsFragment.inject(){
    DaggerLastFmCredentialsComponent.factory()
        .create(coreComponent())
        .inject(this)
}

@Component(dependencies = [CoreComponent::class])
@PerFragment
interface LastFmCredentialsComponent : InjectionHelper<LastFmCredentialsFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): LastFmCredentialsComponent
    }

}