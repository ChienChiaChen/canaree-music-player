@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.ui.extensions

import android.app.AlertDialog
import android.view.WindowManager
import dev.olog.msc.shared.utils.isOreo

@Suppress("DEPRECATION")
inline fun AlertDialog.enableForService(){
    val windowType = if (isOreo())
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    window.setType(windowType)
}