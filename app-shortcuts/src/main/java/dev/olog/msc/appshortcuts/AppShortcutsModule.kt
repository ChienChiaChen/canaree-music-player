package dev.olog.msc.appshortcuts

import android.content.Context
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.Classes
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.shared.utils.isNougat_MR1
import javax.inject.Singleton

@Module
class AppShortcutsModule {

    @Provides
    @Singleton
    internal fun provideShortcuts(
            @ApplicationContext context: Context,
            @ProcessLifecycle lifecycle: Lifecycle,
            classes: Classes): AppShortcuts {
        if (isNougat_MR1()){
            return AppShortcutsImpl25(context, lifecycle, classes)
        }
        return AppShortcutsStub(context, lifecycle, classes)
    }

}