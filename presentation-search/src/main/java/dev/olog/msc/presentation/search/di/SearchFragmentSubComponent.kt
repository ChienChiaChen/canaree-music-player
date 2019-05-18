package dev.olog.msc.presentation.search.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.search.SearchFragment

@Subcomponent(
    modules = arrayOf(
        SearchFragmentModule::class
    )
)
@PerFragment
interface SearchFragmentSubComponent : AndroidInjector<SearchFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SearchFragment>() {

        abstract fun module(module: SearchFragmentModule): Builder

        override fun seedInstance(instance: SearchFragment) {
            module(SearchFragmentModule(instance))
        }
    }

}