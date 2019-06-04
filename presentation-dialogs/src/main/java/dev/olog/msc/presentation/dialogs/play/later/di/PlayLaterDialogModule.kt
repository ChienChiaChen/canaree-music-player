package dev.olog.msc.presentation.dialogs.play.later.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun PlayLaterDialog.inject() {
    DaggerPlayLaterDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class PlayLaterDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayLaterDialogViewModel::class)
    abstract fun provideViewModel(viewModel: PlayLaterDialogViewModel): ViewModel

}
