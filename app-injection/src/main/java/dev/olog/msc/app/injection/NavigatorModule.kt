package dev.olog.msc.app.injection

import dagger.Binds
import dagger.Module
import dev.olog.msc.app.injection.navigator.MainPopupNavigator
import dev.olog.msc.app.injection.navigator.NavigatorAboutImpl
import dev.olog.msc.presentation.navigator.IPopupNavigator
import dev.olog.msc.presentation.navigator.NavigatorAbout
import javax.inject.Singleton

@Module
internal abstract class NavigatorModule {

    @Binds
    @Singleton
    abstract fun provideNavigatorAbout(impl: NavigatorAboutImpl): NavigatorAbout

    @Binds
    @Singleton
    abstract fun providePopupNavigator(impl: MainPopupNavigator): IPopupNavigator

}