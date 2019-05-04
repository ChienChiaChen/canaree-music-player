package dev.olog.msc.presentation.preferences.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.pro.ProModule

@Module(includes = [ProModule::class])
class PreferencesActivityModule(private val activity: PreferencesActivity) {

    @Provides
    internal fun provideActivity() : AppCompatActivity = activity

}