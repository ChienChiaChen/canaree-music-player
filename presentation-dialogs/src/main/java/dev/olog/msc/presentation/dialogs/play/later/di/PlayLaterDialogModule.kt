package dev.olog.msc.presentation.dialogs.play.later.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialogViewModel

@Module
abstract class PlayLaterDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): PlayLaterDialog

    @Binds
    @IntoMap
    @ViewModelKey(PlayLaterDialogViewModel::class)
    abstract fun provideViewModel(viewModel: PlayLaterDialogViewModel): ViewModel

}