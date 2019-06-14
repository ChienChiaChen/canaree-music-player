package dev.olog.msc.app.shortcuts

import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.utils.isNougat_MR1
import javax.inject.Singleton

@Module
class AppShortcutsModule {

    @Provides
    @Singleton
    fun provideShortcuts(@ApplicationContext context: Context, @ProcessLifecycle lifecycle: Lifecycle): AppShortcuts {
        if (isNougat_MR1()){
            return AppShortcutsImpl25(context, lifecycle)
        }
        return AppShortcutsStub(context, lifecycle)
    }

}