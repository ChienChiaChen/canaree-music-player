
package dev.olog.msc.presentation.about.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.app.injection.coreComponent
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.about.AboutActivityViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

fun AboutActivity.inject() {
    DaggerAboutActivityComponent.factory()
        .create(this, coreComponent())
        .inject(this)
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
