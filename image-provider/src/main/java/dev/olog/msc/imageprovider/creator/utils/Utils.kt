package dev.olog.msc.imageprovider.creator.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import dev.olog.msc.shared.utils.assertMainThread

private var isLowMemory : Boolean? = null

internal fun isLowMemoryDevice(context: Context): Boolean {
    assertMainThread()
    if (isLowMemory == null){
        val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        isLowMemory = manager.isLowRamDevice
    }
    return isLowMemory!!
}