package dev.olog.msc.presentation.dialogs.ringtone.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun SetRingtoneDialog.inject() {
    DaggerSetRingtoneDialogComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}


@Module
abstract class SetRingtoneDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(SetRingtoneDialogViewModel::class)
    abstract fun provideViewModel(viewModel: SetRingtoneDialogViewModel): ViewModel

}
