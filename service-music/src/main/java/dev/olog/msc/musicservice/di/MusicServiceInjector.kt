package dev.olog.msc.musicservice.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.musicservice.MusicService


@Module(subcomponents = arrayOf(MusicServiceSubComponent::class))
abstract class MusicServiceInjector {

    @Binds
    @IntoMap
    @ClassKey(MusicService::class)
    internal abstract fun injectorFactory(builder: MusicServiceSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
