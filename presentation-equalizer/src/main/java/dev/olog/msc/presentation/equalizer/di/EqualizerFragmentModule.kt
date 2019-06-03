package dev.olog.msc.presentation.equalizer.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.equalizer.EqualizerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun EqualizerFragment.inject() {
    DaggerEqualizerFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class EqualizerFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(EqualizerFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: EqualizerFragmentViewModel): ViewModel
}
