package dev.olog.msc.presentation.create.playlist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.create.playlist.CreatePlaylistViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class CreatePlaylistModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreatePlaylistViewModel::class)
    abstract fun provideViewModel(viewModel: CreatePlaylistViewModel): ViewModel

}