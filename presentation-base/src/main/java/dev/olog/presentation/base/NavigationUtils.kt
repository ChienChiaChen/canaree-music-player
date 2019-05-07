package dev.olog.presentation.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import dev.olog.msc.shared.extensions.toast

fun openPlayStore(activity: Activity){
    val uri = Uri.parse("market://details?id=${activity.packageName}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    if (intent.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(intent)
    } else {
        activity.toast("Play Store not found")
    }
}