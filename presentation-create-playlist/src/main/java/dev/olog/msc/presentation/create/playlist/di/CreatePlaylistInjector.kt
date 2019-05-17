package dev.olog.msc.presentation.create.playlist.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment

@Module(subcomponents = arrayOf(CreatePlaylistSubComponent::class))
abstract class CreatePlaylistInjector {

    @Binds
    @IntoMap
    @ClassKey(CreatePlaylistFragment::class)
    internal abstract fun injectorFactory(builder: CreatePlaylistSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
