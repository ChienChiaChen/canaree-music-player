package dev.olog.msc.presentation.dialogs.rename.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.rename.RenameDialog


@Subcomponent(modules = arrayOf(
        RenameDialogModule::class
))
@PerFragment
interface RenameDialogSubComponent : AndroidInjector<RenameDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RenameDialog>() {

        abstract fun module(module: RenameDialogModule): Builder

        override fun seedInstance(instance: RenameDialog) {
            module(RenameDialogModule(instance))
        }
    }

}