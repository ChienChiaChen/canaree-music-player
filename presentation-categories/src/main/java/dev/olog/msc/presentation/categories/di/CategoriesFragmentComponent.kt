package dev.olog.msc.presentation.categories.di

import dagger.Component
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.core.dagger.scope.PerFragment
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment

@Component(dependencies = [CoreComponent::class])
@PerFragment
interface CategoriesFragmentComponent {

    fun inject(instance: CategoriesFragment)
    fun inject(instance: CategoriesPodcastFragment)

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): CategoriesFragmentComponent
    }

}