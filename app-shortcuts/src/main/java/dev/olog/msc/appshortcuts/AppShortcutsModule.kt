package dev.olog.msc.appshortcuts

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import javax.inject.Singleton

@Module
class AppShortcutsModule {

    @Provides
    @Singleton
    internal fun provideShortcuts(
        @ApplicationContext context: Context
    ): AppShortcuts {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return AppShortcutsImpl25(context)
        }
        return AppShortcutsStub(context)
    }

}