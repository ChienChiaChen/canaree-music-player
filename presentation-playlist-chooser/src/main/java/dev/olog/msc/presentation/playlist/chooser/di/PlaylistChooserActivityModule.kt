package dev.olog.msc.presentation.playlist.chooser.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playlist.chooser.PlaylistChooserActivityViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class PlaylistChooserActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistChooserActivityViewModel::class)
    abstract fun provideViewModel(viewModel: PlaylistChooserActivityViewModel): ViewModel

}