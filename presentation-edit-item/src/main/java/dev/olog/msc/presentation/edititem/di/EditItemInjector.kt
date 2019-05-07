package dev.olog.msc.presentation.edititem.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edititem.EditItemViewModel
import dev.olog.msc.presentation.edititem.album.di.EditAlbumFragmentInjector
import dev.olog.msc.presentation.edititem.artist.di.EditArtistFragmentInjector
import dev.olog.msc.presentation.edititem.track.di.EditTrackFragmentInjector
import dev.olog.presentation.base.ViewModelKey

@Module(includes = [
    EditTrackFragmentInjector::class,
    EditAlbumFragmentInjector::class,
    EditArtistFragmentInjector::class
])
abstract class EditItemInjector {
    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    abstract fun provideViewModel(viewModel: EditItemViewModel): ViewModel
}