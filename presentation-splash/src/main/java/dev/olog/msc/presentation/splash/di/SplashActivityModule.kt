package dev.olog.msc.presentation.splash.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.splash.SplashActivity

@Module
abstract class SplashActivityModule {

    @ContributesAndroidInjector
    abstract fun provideSplashActivity(): SplashActivity

}