package dev.olog.msc.presentation.tabs.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.tabs.TabFragment
import dev.olog.msc.presentation.tabs.TabFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun TabFragment.inject() {
    DaggerTabFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
abstract class TabFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(TabFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: TabFragmentViewModel): ViewModel

}