package dev.olog.msc.presentation.playing.queue.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel

@Module
class PlayingQueueFragmentModule(private val fragment: PlayingQueueFragment) {


    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Module
    companion object {
        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(PlayingQueueFragmentViewModel::class)
        internal fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel {
            return viewModel
        }
    }

}