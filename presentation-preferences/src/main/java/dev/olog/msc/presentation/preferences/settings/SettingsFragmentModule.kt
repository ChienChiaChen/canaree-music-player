package dev.olog.msc.presentation.preferences.settings

import dagger.Module
import dev.olog.msc.app.injection.coreComponent

fun SettingsFragment.inject() {
    DaggerSettingsFragmentComponent.factory()
        .create(coreComponent())
        .inject(this)
}

@Module
abstract class SettingsFragmentModule {

//    @ContributesAndroidInjector
//    internal abstract fun provideLibraryCategoriesFragment(): LibraryCategoriesFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun provideBlacklistFragment(): BlacklistFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun provideLastFmCredentialsFragment(): LastFmCredentialsFragment
//
//    @ContributesAndroidInjector
//    internal abstract fun providePreferencesFragment(): PreferencesFragment

//    @Binds
//    @IntoMap
//    @ViewModelKey(BlacklistFragmentViewModel::class)
//    internal abstract fun provideViewModel(viewModel: BlacklistFragmentViewModel): ViewModel
}
