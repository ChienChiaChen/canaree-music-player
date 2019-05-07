package dev.olog.msc.presentation.edititem.artist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edititem.artist.EditArtistFragment

@Module(subcomponents = arrayOf(EditArtistFragmentSubComponent::class))
abstract class EditArtistFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(EditArtistFragment::class)
    internal abstract fun injectorFactory(builder: EditArtistFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
