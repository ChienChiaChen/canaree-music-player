package dev.olog.msc.presentation.preferences.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.preferences.PreferencesFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragmentViewModel
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.preferences.credentials.LastFmCredentialsFragment
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class PreferenceActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideLibraryCategoriesFragment(): LibraryCategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideBlacklistFragment(): BlacklistFragment

    @ContributesAndroidInjector
    abstract fun provideLastFmCredentialsFragment(): LastFmCredentialsFragment

    @ContributesAndroidInjector
    abstract fun providePreferencesFragment(): PreferencesFragment

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(BlacklistFragmentViewModel::class)
        fun provideViewModel(viewModel: BlacklistFragmentViewModel): ViewModel {
            return viewModel
        }

    }

}