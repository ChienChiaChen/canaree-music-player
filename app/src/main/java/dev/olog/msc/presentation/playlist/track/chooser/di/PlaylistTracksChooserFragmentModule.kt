package dev.olog.msc.presentation.playlist.track.chooser.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragment
import dev.olog.msc.presentation.playlist.track.chooser.PlaylistTracksChooserFragmentViewModel

@Module(includes = [PlaylistTracksChooserFragmentModule.Binding::class])
class PlaylistTracksChooserFragmentModule(private val fragment: PlaylistTracksChooserFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Provides
    fun providePlaylistType(): PlaylistType {
        val type = fragment.arguments!!.getInt(PlaylistTracksChooserFragment.ARGUMENT_PLAYLIST_TYPE)
        return PlaylistType.values()[type]
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(PlaylistTracksChooserFragmentViewModel::class)
        fun provideViewModel(viewModel: PlaylistTracksChooserFragmentViewModel): ViewModel

    }

}