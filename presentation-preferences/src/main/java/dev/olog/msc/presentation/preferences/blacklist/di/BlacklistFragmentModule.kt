package dev.olog.msc.presentation.preferences.blacklist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun BlacklistFragment.inject(){

}

@Module
abstract class BlacklistFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(BlacklistFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: BlacklistFragmentViewModel): ViewModel

}