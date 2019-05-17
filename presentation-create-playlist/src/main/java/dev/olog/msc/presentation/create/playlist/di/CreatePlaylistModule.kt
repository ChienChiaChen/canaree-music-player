package dev.olog.msc.presentation.create.playlist.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment
import dev.olog.msc.presentation.create.playlist.CreatePlaylistViewModel

@Module
class CreatePlaylistModule(private val fragment: CreatePlaylistFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(CreatePlaylistViewModel::class)
        fun provideViewModel(viewModel: CreatePlaylistViewModel): ViewModel {
            return viewModel
        }

    }

}