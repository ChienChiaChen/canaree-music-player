package dev.olog.msc.musicservice.di

import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.MusicService


@Module(subcomponents = [MusicServiceSubComponent::class])
abstract class MusicServiceInjector {

    @Binds
    @IntoMap
    @ClassKey(MusicService::class)
    internal abstract fun injectorFactory(builder: MusicServiceSubComponent.Factory): AndroidInjector.Factory<*>

}

@Subcomponent(
    modules = [
        MusicServiceModule::class,
        NotificationModule::class
    ]
)
@PerService
interface MusicServiceSubComponent : AndroidInjector<MusicService> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MusicService> {
        override fun create(@BindsInstance instance: MusicService): AndroidInjector<MusicService>
    }
}