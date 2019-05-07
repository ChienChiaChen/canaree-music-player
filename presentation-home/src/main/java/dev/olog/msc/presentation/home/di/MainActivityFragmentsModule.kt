package dev.olog.msc.presentation.home.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragment

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideMiniPlayer() : MiniPlayerFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesFragment(): CategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesPodcastFragment(): CategoriesPodcastFragment
}