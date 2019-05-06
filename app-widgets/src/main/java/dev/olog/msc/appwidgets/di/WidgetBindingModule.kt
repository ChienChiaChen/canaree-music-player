package dev.olog.msc.appwidgets.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.appwidgets.base.WidgetColored
import dev.olog.msc.appwidgets.queue.QueueWidgetService
import dev.olog.msc.appwidgets.queue.WidgetColoredWithQueue

@Module
abstract class WidgetBindingModule {

    @ContributesAndroidInjector
    abstract fun provideWidgetColored() : WidgetColored

    @ContributesAndroidInjector
    abstract fun provideWidgetColoredWithQueue() : WidgetColoredWithQueue

    @ContributesAndroidInjector
    abstract fun provideWidgetQueueService(): QueueWidgetService

}