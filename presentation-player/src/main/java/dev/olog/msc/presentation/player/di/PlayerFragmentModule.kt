package dev.olog.msc.presentation.player.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.player.PlayerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class PlayerFragmentModule {

    @ContributesAndroidInjector
    abstract fun proviewFragment(): PlayerFragment

    @Binds
    @IntoMap
    @ViewModelKey(PlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: PlayerFragmentViewModel): ViewModel

}