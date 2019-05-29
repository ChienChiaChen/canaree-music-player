

package dev.olog.msc.presentation.search.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

internal fun SearchFragment.inject() {
    DaggerSearchFragmentComponent.factory()
        .create(requireActivity().coreComponent())
        .inject(this)
}

@Module
internal abstract class SearchFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: SearchFragmentViewModel): ViewModel

}
