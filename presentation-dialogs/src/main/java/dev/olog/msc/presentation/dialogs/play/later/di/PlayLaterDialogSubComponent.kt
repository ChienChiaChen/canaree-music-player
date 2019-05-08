package dev.olog.msc.presentation.dialogs.play.later.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog

@Subcomponent(modules = arrayOf(
        PlayLaterDialogModule::class
))
@PerFragment
interface PlayLaterDialogSubComponent : AndroidInjector<PlayLaterDialog> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PlayLaterDialog>() {

        abstract fun module(module: PlayLaterDialogModule): Builder

        override fun seedInstance(instance: PlayLaterDialog) {
            module(PlayLaterDialogModule(instance))
        }
    }

}