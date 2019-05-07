package dev.olog.msc.presentation.edititem.artist.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.edititem.artist.EditArtistFragment

@Subcomponent(modules = arrayOf(
        EditArtistFragmentModule::class
))
@PerFragment
interface EditArtistFragmentSubComponent : AndroidInjector<EditArtistFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditArtistFragment>() {

        abstract fun module(module: EditArtistFragmentModule): Builder

        override fun seedInstance(instance: EditArtistFragment) {
            module(EditArtistFragmentModule(instance))
        }
    }

}