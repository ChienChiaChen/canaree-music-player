package dev.olog.msc.presentation.preferences.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.preferences.PreferencesFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragmentViewModel
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.preferences.credentials.LastFmCredentialsFragment
import dev.olog.msc.pro.ProModule
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(
    subcomponents = [PreferencesActivitySubComponent::class]
)
abstract class PreferencesActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(PreferencesActivity::class)
    internal abstract fun injectorFactory(builder: PreferencesActivitySubComponent.Factory): AndroidInjector.Factory<*>


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

@Subcomponent(modules = [ProModule::class, PreferenceActivityModule::class])
@PerActivity
interface PreferencesActivitySubComponent : AndroidInjector<PreferencesActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<PreferencesActivity> {
        override fun create(@BindsInstance instance: PreferencesActivity): PreferencesActivitySubComponent
    }

}