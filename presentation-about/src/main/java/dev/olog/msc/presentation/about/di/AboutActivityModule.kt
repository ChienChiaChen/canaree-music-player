package dev.olog.msc.presentation.about.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.pro.ProModule

@Module(includes = [ProModule::class])
class AboutActivityModule(
        private val activity: AboutActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

}