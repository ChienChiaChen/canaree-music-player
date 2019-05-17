package dev.olog.msc.presentation.create.playlist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment

@Subcomponent(
    modules = arrayOf(
        CreatePlaylistModule::class
    )
)
@PerFragment
interface CreatePlaylistSubComponent : AndroidInjector<CreatePlaylistFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CreatePlaylistFragment>() {

        abstract fun module(module: CreatePlaylistModule): Builder

        override fun seedInstance(instance: CreatePlaylistFragment) {
            module(CreatePlaylistModule(instance))
        }
    }

}
