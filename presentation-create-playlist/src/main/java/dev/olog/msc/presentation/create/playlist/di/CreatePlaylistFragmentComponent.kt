package dev.olog.msc.presentation.create.playlist.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment

@Component(
    modules = [
        ViewModelModule::class,
        CreatePlaylistModule::class
    ], dependencies = [CoreComponent::class]
)
@PerActivity
interface CreatePlaylistFragmentComponent : InjectionHelper<CreatePlaylistFragment> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): CreatePlaylistFragmentComponent
    }

}