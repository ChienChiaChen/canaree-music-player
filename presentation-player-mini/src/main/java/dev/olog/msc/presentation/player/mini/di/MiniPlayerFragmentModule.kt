package dev.olog.msc.presentation.player.mini.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.player.mini.MiniPlayerFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class MiniPlayerFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun provideMiniPlayer(): MiniPlayerFragment

    @Binds
    @IntoMap
    @ViewModelKey(MiniPlayerFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: MiniPlayerFragmentViewModel): ViewModel

}