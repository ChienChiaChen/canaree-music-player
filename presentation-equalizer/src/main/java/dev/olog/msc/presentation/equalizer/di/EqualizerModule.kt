package dev.olog.msc.presentation.equalizer.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.equalizer.EqualizerFragment

@Module
abstract class EqualizerModule {

    @ContributesAndroidInjector
    abstract fun provideEqualizerFragment(): EqualizerFragment

}