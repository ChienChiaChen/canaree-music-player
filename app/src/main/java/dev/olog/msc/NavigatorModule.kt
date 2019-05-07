package dev.olog.msc

import dagger.Binds
import dagger.Module
import dev.olog.msc.presentation.navigator.*
import javax.inject.Singleton

@Module
abstract class NavigatorModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: NavigatorImpl): Navigator

    @Binds
    @Singleton
    abstract fun provideNavigatorAbout(impl: NavigatorAboutImpl): NavigatorAbout

    @Binds
    @Singleton
    abstract fun providePopupNavigator(impl: MainPopupNavigator): IPopupNavigator

}