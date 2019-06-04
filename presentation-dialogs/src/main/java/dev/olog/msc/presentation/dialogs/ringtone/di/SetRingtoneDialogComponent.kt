package dev.olog.msc.presentation.dialogs.ringtone.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog

@Component(
    modules = [
        ViewModelModule::class,
        SetRingtoneDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface SetRingtoneDialogComponent : InjectionHelper<SetRingtoneDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): SetRingtoneDialogComponent
    }

}