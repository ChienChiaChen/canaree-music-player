package dev.olog.msc.presentation.dialogs.playlist.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialog

@Component(
    modules = [
        ViewModelModule::class,
        NewPlaylistDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface NewPlaylistDialogComponent : InjectionHelper<NewPlaylistDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): NewPlaylistDialogComponent
    }

}