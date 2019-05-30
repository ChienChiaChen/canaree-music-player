package dev.olog.msc.presentation.playing.queue.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment

@Component(
    modules = [
        PlayingQueueFragmentModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface PlayingQueueFragmentComponent : InjectionHelper<PlayingQueueFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): PlayingQueueFragmentComponent
    }

}