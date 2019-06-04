package dev.olog.msc.presentation.offlinelyrics.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.offlinelyrics.OfflineLyricsFragment

fun OfflineLyricsFragment.inject() {
    DaggerOfflineLyricsFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Component(dependencies = [CoreComponent::class])
@PerFragment
interface OfflineLyricsFragmentComponent : InjectionHelper<OfflineLyricsFragment> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): OfflineLyricsFragmentComponent
    }

}
