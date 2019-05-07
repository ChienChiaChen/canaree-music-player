package dev.olog.msc

import dagger.Binds
import dagger.Module
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.presentation.navigator.NavigatorAboutImpl
import dev.olog.msc.presentation.navigator.NavigatorImpl
import javax.inject.Singleton

@Module
abstract class NavigatorModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: NavigatorImpl): Navigator

    @Binds
    @Singleton
    abstract fun provideNavigatorAbout(impl: NavigatorAboutImpl): NavigatorAbout

}