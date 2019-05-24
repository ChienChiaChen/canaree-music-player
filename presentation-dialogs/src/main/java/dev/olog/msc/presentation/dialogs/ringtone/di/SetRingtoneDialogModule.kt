package dev.olog.msc.presentation.dialogs.ringtone.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class SetRingtoneDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): SetRingtoneDialog

    @Binds
    @IntoMap
    @ViewModelKey(SetRingtoneDialogViewModel::class)
    abstract fun provideViewModel(viewModel: SetRingtoneDialogViewModel): ViewModel

}