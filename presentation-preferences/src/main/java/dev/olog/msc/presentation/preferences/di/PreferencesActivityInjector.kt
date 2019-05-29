package dev.olog.msc.presentation.preferences.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.preferences.PreferencesFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragmentViewModel
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.preferences.credentials.LastFmCredentialsFragment
import dev.olog.msc.shared.dagger.ViewModelKey

fun PreferencesActivity.inject() {
    DaggerPreferencesActivityComponent.factory()
        .create(this, coreComponent())
        .inject(this)
}

@Module
abstract class PreferenceActivityModule {

    @Binds
    internal abstract fun provideActivity(activity: PreferencesActivity): AppCompatActivity

    @ContributesAndroidInjector
    internal abstract fun provideLibraryCategoriesFragment(): LibraryCategoriesFragment

    @ContributesAndroidInjector
    internal abstract fun provideBlacklistFragment(): BlacklistFragment

    @ContributesAndroidInjector
    internal abstract fun provideLastFmCredentialsFragment(): LastFmCredentialsFragment

    @ContributesAndroidInjector
    internal abstract fun providePreferencesFragment(): PreferencesFragment

    @Binds
    @IntoMap
    @ViewModelKey(BlacklistFragmentViewModel::class)
    internal abstract fun provideViewModel(viewModel: BlacklistFragmentViewModel): ViewModel
}
