package dev.olog.msc.presentation.preferences.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.preferences.PreferencesFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.preferences.credentials.LastFmCredentialsFragment

@Module
abstract class PreferenceActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideLibraryCategoriesFragment() : LibraryCategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideBlacklistFragment() : BlacklistFragment

    @ContributesAndroidInjector
    abstract fun provideLastFmCredentialsFragment() : LastFmCredentialsFragment

    @ContributesAndroidInjector
    abstract fun providePreferencesFragment() : PreferencesFragment

}