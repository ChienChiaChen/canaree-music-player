package dev.olog.msc.app.injection

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.offlinelyrics.domain.ILyricsFromMetadata
import javax.inject.Singleton

@Module
internal abstract class CoreModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(app: Application): Context

    @Binds
    @Singleton
    internal abstract fun provideEncryoter(impl: LastFmEncrypter): IEncrypter

    @Binds
    internal abstract fun provideLyricsFromMetadata(impl: LyricsFromMetadata): ILyricsFromMetadata

    @Module
    companion object {
        @Provides
        @JvmStatic
        @ProcessLifecycle
        internal fun provideAppLifecycle(): Lifecycle {
            return ProcessLifecycleOwner.get().lifecycle
        }

        @Provides
        @JvmStatic
        internal fun provideWidgetsClasses(): WidgetClasses {
            return object : WidgetClasses {
                override fun get(): List<Class<*>> {
                    return listOf(
//                        WidgetColored::class.java TODO
                    )
                }
            }
        }
    }

}