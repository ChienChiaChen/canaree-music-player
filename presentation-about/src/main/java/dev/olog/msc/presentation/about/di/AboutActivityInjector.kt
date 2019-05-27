package dev.olog.msc.presentation.about.di

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
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.about.AboutActivityViewModel
import dev.olog.msc.pro.ProModule
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(
    subcomponents = [AboutActivitySubComponent::class]
)
abstract class AboutActivityInjector {

    @Binds
    @IntoMap
    @ClassKey(AboutActivity::class)
    internal abstract fun injectorFactory(builder: AboutActivitySubComponent.Factory): AndroidInjector.Factory<*>

}

@Module
abstract class AboutActivityModule {
    @Binds
    internal abstract fun provideSupportActivity(activity: AboutActivity): AppCompatActivity

    @Binds
    @IntoMap
    @ViewModelKey(AboutActivityViewModel::class)
    abstract fun provideViewModel(viewModel: AboutActivityViewModel): ViewModel
}

@Subcomponent(modules = [ProModule::class, AboutActivityModule::class])
@PerActivity
interface AboutActivitySubComponent : AndroidInjector<AboutActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<AboutActivity> {
        override fun create(@BindsInstance instance: AboutActivity): AboutActivitySubComponent
    }


}