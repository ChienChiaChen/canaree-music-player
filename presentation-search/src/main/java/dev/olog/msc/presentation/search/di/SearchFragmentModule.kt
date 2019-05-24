package dev.olog.msc.presentation.search.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class SearchFragmentModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): SearchFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: SearchFragmentViewModel): ViewModel

}