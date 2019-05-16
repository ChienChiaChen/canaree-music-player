package dev.olog.msc.presentation.dialogs.playlist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialogViewModel

@Module
abstract class ClearPlaylistDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): ClearPlaylistDialog

    @Binds
    @IntoMap
    @ViewModelKey(ClearPlaylistDialogViewModel::class)
    abstract fun provideViewModel(viewModel: ClearPlaylistDialogViewModel): ViewModel

}