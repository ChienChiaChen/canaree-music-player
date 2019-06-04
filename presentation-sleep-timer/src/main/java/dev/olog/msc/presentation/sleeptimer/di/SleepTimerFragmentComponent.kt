package dev.olog.msc.presentation.sleeptimer.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.sleeptimer.SleepTimerPickerDialog

fun SleepTimerPickerDialog.inject() {
    DaggerSleepTimerFragmentComponent.factory()
            .create(requireActivity().coreComponent())
            .inject(this)
}

@Component(dependencies = [CoreComponent::class])
@PerFragment
interface SleepTimerFragmentComponent : InjectionHelper<SleepTimerPickerDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): SleepTimerFragmentComponent
    }

}
