package dev.olog.msc.presentation.dialogs.favorite.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialog

@Subcomponent(modules = arrayOf(
        AddFavoriteDialogModule::class
))
@PerFragment
interface AddFavoriteDialogSubComponent : AndroidInjector<AddFavoriteDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddFavoriteDialog>() {

        abstract fun module(module: AddFavoriteDialogModule): Builder

        override fun seedInstance(instance: AddFavoriteDialog) {
            module(AddFavoriteDialogModule(instance))
        }
    }

}