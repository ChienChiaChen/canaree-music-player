package dev.olog.msc.presentation.home.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.presentation.home.MainActivityViewModel
import dev.olog.msc.shared.dagger.ViewModelKey

@Module
abstract class MainActivityModule {
    @Binds
    internal abstract fun provideSupportActivity(activity: MainActivity): AppCompatActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun provideViewModel(viewModel: MainActivityViewModel): ViewModel
}
