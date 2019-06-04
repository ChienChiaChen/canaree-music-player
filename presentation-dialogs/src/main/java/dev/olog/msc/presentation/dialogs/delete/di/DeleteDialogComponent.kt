package dev.olog.msc.presentation.dialogs.delete.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.delete.DeleteDialog

@Component(
    modules = [
        ViewModelModule::class,
        DeleteDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface DeleteDialogComponent : InjectionHelper<DeleteDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): DeleteDialogComponent
    }

}