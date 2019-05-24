package dev.olog.msc.presentation.playing.queue.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class PlayingQueueFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): PlayingQueueFragment

    @Binds
    @IntoMap
    @ViewModelKey(PlayingQueueFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

}