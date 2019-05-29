package dev.olog.msc.musicservice.di

import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.MusicService

@Component(
    modules = [
        MusicServiceModule::class,
        NotificationModule::class
    ], dependencies = [CoreComponent::class]
)
@PerService
interface MusicServiceComponent : InjectionHelper<MusicService> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance service: MusicService, component: CoreComponent): MusicServiceComponent
    }

}