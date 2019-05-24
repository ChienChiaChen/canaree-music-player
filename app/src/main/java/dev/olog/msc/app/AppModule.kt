package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Module
import dagger.Provides
import dev.olog.msc.LastFmEncrypter
import dev.olog.msc.LyricsFromMetadata
import dev.olog.msc.PrefsKeysImpl
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
class AppModule(private val app: App) {

    @Provides
    @ApplicationContext
    internal fun provideContext(): Context = app

    @Provides
    internal fun provideResources(): Resources = app.resources

    @Provides
    internal fun provideApplication(): Application = app

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
                    dev.olog.msc.appwidgets.base.WidgetColored::class.java
                )
            }
        }
    }

    @Provides
    fun provideConnectivityManager(): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideAlarmManager(): AlarmManager {
        return app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideEncryoter(impl: LastFmEncrypter): IEncrypter {
        return impl
    }

    @Provides
    fun providePrefsKeys(impl: PrefsKeysImpl): PrefsKeys {
        return impl
    }

    @Provides
    fun provideLyricsFromMetadata(impl: LyricsFromMetadata): ILyricsFromMetadata {
        return impl
    }

    @Provides
    fun provideDarkMode(impl: DarkMode): IDarkMode {
        return impl
    }

    @Provides
    fun providePlayerTheme(impl: PlayerTheme): IPlayerTheme {
        return impl
    }

    @Provides
    fun provideImmersive(impl: Immersive): IImmersive {
        return impl
    }

}