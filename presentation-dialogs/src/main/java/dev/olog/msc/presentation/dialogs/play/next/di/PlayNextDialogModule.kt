package dev.olog.msc.presentation.dialogs.play.next.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialog
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun PlayNextDialog.inject() {
    DaggerPlayNextDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class PlayNextDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayNextDialogViewModel::class)
    abstract fun provideViewModel(viewModel: PlayNextDialogViewModel): ViewModel

}
