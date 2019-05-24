package dev.olog.msc.presentation.about.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.about.AboutActivityViewModel
import dev.olog.msc.pro.ProModule
import dev.olog.msc.shared.dagger.ViewModelKey

@Module(includes = [ProModule::class])
class AboutActivityModule(
    private val activity: AboutActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Module
    companion object {
        @Provides
        @IntoMap
        @JvmStatic
        @ViewModelKey(AboutActivityViewModel::class)
        fun provideViewModel(viewModel: AboutActivityViewModel): ViewModel {
            return viewModel
        }
    }

}