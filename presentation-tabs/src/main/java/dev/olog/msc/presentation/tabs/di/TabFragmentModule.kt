package dev.olog.msc.presentation.tabs.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.tabs.TabFragment
import dev.olog.msc.presentation.tabs.TabFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

// used by MainActivitySubComponent
@Module
abstract class TabFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun provideFragment(): TabFragment

    @Binds
    @IntoMap
    @ViewModelKey(TabFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: TabFragmentViewModel): ViewModel

}