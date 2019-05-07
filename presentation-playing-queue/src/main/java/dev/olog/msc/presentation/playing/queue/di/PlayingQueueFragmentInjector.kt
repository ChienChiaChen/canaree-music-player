package dev.olog.msc.presentation.playing.queue.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel
import dev.olog.presentation.base.ViewModelKey

@Module
abstract class PlayingQueueFragmentInjector {

    @Binds
    @IntoMap
    @ViewModelKey(PlayingQueueFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

}