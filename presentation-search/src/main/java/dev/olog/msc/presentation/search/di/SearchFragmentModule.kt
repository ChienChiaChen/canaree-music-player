package dev.olog.msc.presentation.search.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel

@Module
class SearchFragmentModule(private val fragment: SearchFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    @PerFragment
    fun provideRecycledViewPool() = androidx.recyclerview.widget.RecyclerView.RecycledViewPool()

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(SearchFragmentViewModel::class)
        internal fun provideViewModel(viewModel: SearchFragmentViewModel): ViewModel {
            return viewModel
        }

    }

}