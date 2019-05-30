package dev.olog.msc.presentation.search.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.app.injection.InjectionHelper
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.search.SearchFragment

@Component(
    modules = [
        SearchFragmentModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
@PerFragment
interface SearchFragmentComponent : InjectionHelper<SearchFragment> {

    @Component.Factory
    interface Factory {
        fun create(component: CoreComponent): SearchFragmentComponent
    }

}