package dev.olog.msc.presentation.dialogs.play.next.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialog
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialogViewModel

@Module
abstract class PlayNextDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): PlayNextDialog

    @Binds
    @IntoMap
    @ViewModelKey(PlayNextDialogViewModel::class)
    abstract fun provideViewModel(viewModel: PlayNextDialogViewModel): ViewModel

}