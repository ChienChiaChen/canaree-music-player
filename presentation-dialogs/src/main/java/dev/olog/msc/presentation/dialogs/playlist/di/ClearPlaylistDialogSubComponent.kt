package dev.olog.msc.presentation.dialogs.playlist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog

@Subcomponent(modules = arrayOf(
        ClearPlaylistDialogModule::class
))
@PerFragment
interface ClearPlaylistDialogSubComponent : AndroidInjector<ClearPlaylistDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ClearPlaylistDialog>() {

        abstract fun module(module: ClearPlaylistDialogModule): Builder

        override fun seedInstance(instance: ClearPlaylistDialog) {
            module(ClearPlaylistDialogModule(instance))
        }
    }

}