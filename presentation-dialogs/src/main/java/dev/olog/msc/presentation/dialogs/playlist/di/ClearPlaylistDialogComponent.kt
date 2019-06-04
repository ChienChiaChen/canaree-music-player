package dev.olog.msc.presentation.dialogs.playlist.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog

@Component(
    modules = [
        ViewModelModule::class,
        ClearPlaylistDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface ClearPlaylistDialogComponent : InjectionHelper<ClearPlaylistDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): ClearPlaylistDialogComponent
    }

}