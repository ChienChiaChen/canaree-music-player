package dev.olog.msc.appshortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.imageprovider.glide.getBitmap
import dev.olog.msc.shared.ShortcutsConstants
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

internal abstract class BaseAppShortcuts(
    protected val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    protected val classes: Classes

) : AppShortcuts, DefaultLifecycleObserver {

    private var disposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun addDetailShortcut(mediaId: MediaId, title: String) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

            disposable.unsubscribe()
            disposable = Completable.create {

                val intent = Intent(context, classes.mainActivity())
                intent.action = ShortcutsConstants.SHORTCUT_DETAIL
                intent.putExtra(ShortcutsConstants.SHORTCUT_DETAIL_MEDIA_ID, mediaId.toString())

                val bitmap = context.getBitmap(mediaId, 128, { circleCrop() })
                val shortcut = ShortcutInfoCompat.Builder(context, title)
                    .setShortLabel(title)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(intent)
                    .build()

                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)

                it.onComplete()

            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onAddedSuccess(context) }, Throwable::printStackTrace)

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
        disposable.unsubscribe()
    }

}