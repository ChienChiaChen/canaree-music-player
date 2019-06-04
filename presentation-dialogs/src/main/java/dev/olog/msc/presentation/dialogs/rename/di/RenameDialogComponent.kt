package dev.olog.msc.presentation.dialogs.rename.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.rename.RenameDialog

@Component(
    modules = [
        ViewModelModule::class,
        RenameDialogModule::class
    ], dependencies = [CoreComponent::class]
)
@PerFragment
interface RenameDialogComponent : InjectionHelper<RenameDialog> {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): RenameDialogComponent
    }

}