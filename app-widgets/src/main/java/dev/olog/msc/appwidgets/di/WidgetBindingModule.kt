package dev.olog.msc.appwidgets.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.appwidgets.base.WidgetColored

@Module
abstract class WidgetBindingModule {

    @ContributesAndroidInjector
    abstract fun provideWidgetColored(): WidgetColored

}