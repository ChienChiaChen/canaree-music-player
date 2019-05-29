

package dev.olog.msc.presentation.player.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.player.PlayerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun PlayerFragment.inject() {
    DaggerPlayerFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class PlayerFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayerFragmentViewModel): ViewModel

}
