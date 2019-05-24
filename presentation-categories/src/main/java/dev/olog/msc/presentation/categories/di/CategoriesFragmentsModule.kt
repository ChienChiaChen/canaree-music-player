package dev.olog.msc.presentation.categories.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment

@Module
abstract class CategoriesFragmentsModule {
    @ContributesAndroidInjector
    abstract fun provideCategoriesFragment(): CategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesPodcastFragment(): CategoriesPodcastFragment
}