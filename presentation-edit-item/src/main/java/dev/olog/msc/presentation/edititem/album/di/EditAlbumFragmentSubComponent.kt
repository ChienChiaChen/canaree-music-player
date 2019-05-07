package dev.olog.msc.presentation.edititem.album.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.edititem.album.EditAlbumFragment

@Subcomponent(modules = arrayOf(
        EditAlbumFragmentModule::class
))
@PerFragment
interface EditAlbumFragmentSubComponent : AndroidInjector<EditAlbumFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditAlbumFragment>() {

        abstract fun module(module: EditAlbumFragmentModule): Builder

        override fun seedInstance(instance: EditAlbumFragment) {
            module(EditAlbumFragmentModule(instance))
        }
    }

}