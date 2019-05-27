package dev.olog.msc.presentation.home.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.presentation.home.MainActivityViewModel
import dev.olog.msc.pro.ProModule
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(
    subcomponents = [MainActivitySubComponent::class]
)
abstract class MainActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    internal abstract fun injectorFactory(builder: MainActivitySubComponent.Factory): AndroidInjector.Factory<*>

}

@Module
abstract class MainActivityModule {
    @Binds
    internal abstract fun provideSupportActivity(activity: MainActivity): AppCompatActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun provideViewModel(viewModel: MainActivityViewModel): ViewModel
}

@Subcomponent(modules = [ProModule::class, MainActivityModule::class])
@PerActivity
interface MainActivitySubComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity> {

        override fun create(@BindsInstance instance: MainActivity): MainActivitySubComponent
    }

}