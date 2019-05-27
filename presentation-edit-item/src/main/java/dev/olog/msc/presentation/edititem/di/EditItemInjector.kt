package dev.olog.msc.presentation.edititem.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edititem.EditItemViewModel
import dev.olog.msc.presentation.edititem.album.EditAlbumFragmentViewModel
import dev.olog.msc.presentation.edititem.artist.EditArtistFragmentViewModel
import dev.olog.msc.presentation.edititem.track.EditTrackFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class EditItemInjector {
    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    abstract fun provideViewModel(viewModel: EditItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditTrackFragmentViewModel::class)
    abstract fun provideEditTrackViewModel(viewModel: EditTrackFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditAlbumFragmentViewModel::class)
    abstract fun provideEditAlbumViewModel(viewModel: EditAlbumFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditArtistFragmentViewModel::class)
    abstract fun provideEditArtistViewModel(viewModel: EditArtistFragmentViewModel): ViewModel
}