package dev.olog.msc.presentation.edititem.album.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edititem.album.EditAlbumFragment

@Module(subcomponents = arrayOf(EditAlbumFragmentSubComponent::class))
abstract class EditAlbumFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(EditAlbumFragment::class)
    internal abstract fun injectorFactory(builder: EditAlbumFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
