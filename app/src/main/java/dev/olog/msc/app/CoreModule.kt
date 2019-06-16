package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.app.widget.defaul.WidgetColored
import dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue
import java.text.Collator
import java.util.*

@Module
internal abstract class CoreModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(app: Application): Context

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
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        @Provides
        @JvmStatic
        fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
            return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        @Provides
        @JvmStatic
        fun provideCollator(): Collator {
            val instance = Collator.getInstance(Locale.UK)
            instance.strength = Collator.SECONDARY
//        instance.decomposition = Collator.CANONICAL_DECOMPOSITION
            return instance
        }

        @Provides
        @JvmStatic
        internal fun provideWidgetsClasses(): WidgetClasses {
            return object : WidgetClasses {
                override fun get(): List<Class<out AppWidgetProvider>> {
                    return listOf(
                        WidgetColored::class.java,
                        WidgetColoredWithQueue::class.java
                    )
                }
            }
        }
    }

}