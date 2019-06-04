package dev.olog.msc.presentation.dialogs.play.later.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog

@Component(
    modules = [
        ViewModelModule::class,
        PlayLaterDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface PlayLaterDialogComponent : InjectionHelper<PlayLaterDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): PlayLaterDialogComponent
    }

}