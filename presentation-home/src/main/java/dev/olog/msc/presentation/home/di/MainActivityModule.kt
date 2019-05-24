package dev.olog.msc.presentation.home.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.presentation.home.MainActivityViewModel
import dev.olog.msc.pro.ProModule
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(includes = [ProModule::class])
class MainActivityModule(
    private val activity: MainActivity
) {

    @Provides
    fun provideInstance() = activity

    @Provides
    internal fun provideSupportActivity(): AppCompatActivity = activity

    @Module
    companion object {

        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(MainActivityViewModel::class)
        internal fun provideViewModel(viewModel: MainActivityViewModel): ViewModel {
            return viewModel
        }

    }

}