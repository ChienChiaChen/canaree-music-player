package dev.olog.msc.presentation.home.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.presentation.home.MainActivityPresenter
import dev.olog.msc.pro.ProModule

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
        @ViewModelKey(MainActivityPresenter::class)
        internal fun provideViewModel(viewModel: MainActivityPresenter): ViewModel {
            return viewModel
        }

    }

}