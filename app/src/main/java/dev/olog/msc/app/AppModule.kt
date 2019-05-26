package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.LastFmEncrypter
import dev.olog.msc.LyricsFromMetadata
import dev.olog.msc.PrefsKeysImpl
import dev.olog.msc.appwidgets.base.WidgetColored
import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.offlinelyrics.domain.ILyricsFromMetadata
import dev.olog.msc.presentation.base.theme.dark.mode.DarkMode
import dev.olog.msc.presentation.base.theme.dark.mode.IDarkMode
import dev.olog.msc.presentation.base.theme.immersive.IImmersive
import dev.olog.msc.presentation.base.theme.immersive.Immersive
import dev.olog.msc.presentation.base.theme.player.theme.IPlayerTheme
import dev.olog.msc.presentation.base.theme.player.theme.PlayerTheme
import javax.inject.Singleton

@Module
class AppModule2 {
    @Provides
    @ProcessLifecycle
    internal fun provideAppLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }

    @Provides
    internal fun provideWidgetsClasses(): WidgetClasses {
        return object : WidgetClasses {
            override fun get(): List<Class<*>> {
                return listOf(
                    WidgetColored::class.java
                )
            }
        }
    }

    @Provides
    internal fun provideResources(app: App): Resources = app.resources

    @Provides
    internal fun provideApplication(app: App): Application = app

    @Provides
    fun provideConnectivityManager(app: App): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideAlarmManager(app: App): AlarmManager {
        return app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(app: App): Context

    @Binds
    @Singleton
    internal abstract fun provideEncryoter(impl: LastFmEncrypter): IEncrypter

    @Binds
    internal abstract fun providePrefsKeys(impl: PrefsKeysImpl): PrefsKeys

    @Binds
    internal abstract fun provideLyricsFromMetadata(impl: LyricsFromMetadata): ILyricsFromMetadata

    @Binds
    internal abstract fun provideDarkMode(impl: DarkMode): IDarkMode

    @Binds
    internal abstract fun providePlayerTheme(impl: PlayerTheme): IPlayerTheme

    @Binds
    internal abstract fun provideImmersive(impl: Immersive): IImmersive

}