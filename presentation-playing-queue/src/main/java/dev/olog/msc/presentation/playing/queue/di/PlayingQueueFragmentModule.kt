

package dev.olog.msc.presentation.playing.queue.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun PlayingQueueFragment.inject() {
    DaggerPlayingQueueFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class PlayingQueueFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayingQueueFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayingQueueFragmentViewModel): ViewModel

}
