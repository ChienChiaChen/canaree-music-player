package dev.olog.msc.appshortcuts

import android.content.Context
import androidx.lifecycle.Lifecycle
import dev.olog.msc.core.Classes
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle

internal class AppShortcutsStub(
        context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        classes: Classes

) : BaseAppShortcuts(context, lifecycle, classes) {

    override fun disablePlay() {
    }

    override fun enablePlay() {
    }
}