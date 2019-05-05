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
import dev.olog.msc.PrefsKeysImpl
import dev.olog.msc.app.glide.GlideImageProvider
import dev.olog.msc.core.Classes
import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.WidgetClasses
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.data.PrefsKeys
import dev.olog.msc.domain.interactor.LyricsFromMetadata
import dev.olog.msc.floatingwindowservice.FloatingWindowService
import dev.olog.msc.imageprovider.IImageProvider
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.offlinelyrics.domain.ILyricsFromMetadata
import dev.olog.msc.presentation.app.widget.defaul.WidgetColored
import dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue
import dev.olog.msc.presentation.main.MainActivity
import java.text.Collator
import java.util.*
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
                        WidgetColored::class.java,
                        WidgetColoredWithQueue::class.java
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
    fun provideCollator(): Collator {
        val instance = Collator.getInstance(Locale.UK)
        instance.strength = Collator.SECONDARY
//        instance.decomposition = Collator.CANONICAL_DECOMPOSITION
        return instance
    }

    @Provides
    @Singleton
    fun provideEncryoter(impl: LastFmEncrypter): IEncrypter {
        return impl
    }

    @Provides
    fun provideClasses(): Classes {
        return object : Classes {
            override fun mainActivity(): Class<*> = MainActivity::class.java
            override fun musicService(): Class<*> = MusicService::class.java
            override fun floatingWindowService(): Class<*> = FloatingWindowService::class.java
        }
    }

    @Provides
    fun provideImageProvider(impl: GlideImageProvider): IImageProvider {
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

}