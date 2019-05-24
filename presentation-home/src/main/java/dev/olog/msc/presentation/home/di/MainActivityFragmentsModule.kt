package dev.olog.msc.presentation.home.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragment
import dev.olog.msc.presentation.player.mini.MiniPlayerFragmentViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideMiniPlayer(): MiniPlayerFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesFragment(): CategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesPodcastFragment(): CategoriesPodcastFragment

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(MiniPlayerFragmentViewModel::class)
        internal fun provideViewModel(viewModel: MiniPlayerFragmentViewModel): ViewModel {
            return viewModel
        }

    }

}