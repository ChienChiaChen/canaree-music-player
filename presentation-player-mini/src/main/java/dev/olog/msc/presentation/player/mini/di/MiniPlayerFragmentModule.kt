

package dev.olog.msc.presentation.player.mini.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.player.mini.MiniPlayerFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun MiniPlayerFragment.inject() {
    DaggerMiniPlayerFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class MiniPlayerFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(MiniPlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: MiniPlayerFragmentViewModel): ViewModel

}
