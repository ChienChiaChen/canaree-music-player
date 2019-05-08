package dev.olog.msc.presentation.dialogs.duplicates.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog

@Subcomponent(modules = arrayOf(
        RemoveDuplicatesDialogModule::class
))
@PerFragment
interface RemoveDuplicatesDialogSubComponent : AndroidInjector<RemoveDuplicatesDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RemoveDuplicatesDialog>() {

        abstract fun module(module: RemoveDuplicatesDialogModule): Builder

        override fun seedInstance(instance: RemoveDuplicatesDialog) {
            module(RemoveDuplicatesDialogModule(instance))
        }
    }

}