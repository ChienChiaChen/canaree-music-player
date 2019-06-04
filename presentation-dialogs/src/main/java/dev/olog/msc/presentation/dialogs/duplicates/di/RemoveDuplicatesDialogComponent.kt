package dev.olog.msc.presentation.dialogs.duplicates.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog

@Component(
    modules = [
        ViewModelModule::class,
        RemoveDuplicatesDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface RemoveDuplicatesDialogComponent : InjectionHelper<RemoveDuplicatesDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): RemoveDuplicatesDialogComponent
    }

}