package dev.olog.msc.presentation.shortcuts.playlist.chooser.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivityAdapter
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivityViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class PlaylistChooserActivityModule {

    @ContributesAndroidInjector
    abstract fun provideActivity(): PlaylistChooserActivityAdapter

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistChooserActivityViewModel::class)
    abstract fun provideViewModel(viewModel: PlaylistChooserActivityViewModel): ViewModel

}