package dev.olog.msc.app.injection

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.appwidgets.base.WidgetColored
import dev.olog.msc.core.IEncrypter
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
abstract class AppModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(app: Application): Context

    @Binds
    @Singleton
    internal abstract fun provideEncryoter(impl: LastFmEncrypter): IEncrypter

    @Binds
    internal abstract fun provideLyricsFromMetadata(impl: LyricsFromMetadata): ILyricsFromMetadata

    @Binds
    internal abstract fun provideDarkMode(impl: DarkMode): IDarkMode

    @Binds
    internal abstract fun providePlayerTheme(impl: PlayerTheme): IPlayerTheme

    @Binds
    internal abstract fun provideImmersive(impl: Immersive): IImmersive

    @Module
    companion object {
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
    }

}