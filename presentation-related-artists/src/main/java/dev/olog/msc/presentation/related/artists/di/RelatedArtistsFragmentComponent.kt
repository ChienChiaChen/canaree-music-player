package dev.olog.msc.presentation.related.artists.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment

@Component(
    modules = [
        RelatedArtistFragmentModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface RelatedArtistsFragmentComponent : InjectionHelper<RelatedArtistFragment> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance fragment: RelatedArtistFragment, component: CoreComponent): RelatedArtistsFragmentComponent
    }

}