package dev.olog.msc.appshortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.imageprovider.glide.getCachedBitmap
import dev.olog.msc.shared.ShortcutsConstants
import dev.olog.msc.shared.core.coroutines.DefaultScope
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.*

internal abstract class BaseAppShortcuts(
    protected val context: Context

) : AppShortcuts, DefaultLifecycleObserver, CoroutineScope by DefaultScope() {

    private var job: Job? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            job?.cancel()
            job = launch {
                val intent = Intent(context, Classes.mainActivity)
                intent.action = ShortcutsConstants.SHORTCUT_DETAIL
                intent.putExtra(ShortcutsConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

                val bitmap = context.getCachedBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(intent)
                    .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                withContext(Dispatchers.Main) {
                    onAddedSuccess(context)
                }
            }

        } else {
            onAddedNotSupported(context)
        }
    }

    private fun onAddedSuccess(context: Context) {
        context.toast(R.string.app_shortcut_added_to_home_screen)
    }

    private fun onAddedNotSupported(context: Context) {
        context.toast(R.string.app_shortcut_add_to_home_screen_not_supported)
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
    }

}