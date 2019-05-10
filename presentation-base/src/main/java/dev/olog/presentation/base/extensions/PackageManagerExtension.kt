@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.presentation.base.extensions

import android.content.Intent
import android.content.pm.PackageManager

inline fun PackageManager.isIntentSafe(intent: Intent): Boolean {
    return queryIntentActivities(intent, 0).isNotEmpty()
}