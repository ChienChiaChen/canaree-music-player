package dev.olog.msc.presentation.dialogs.play.next.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialog

@Component(
    modules = [
        ViewModelModule::class,
        PlayNextDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface PlayNextDialogComponent : InjectionHelper<PlayNextDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): PlayNextDialogComponent
    }

}