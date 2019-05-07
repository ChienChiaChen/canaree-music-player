package dev.olog.msc

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import dev.olog.msc.shared.utils.assertMainThread

private var isLowMemory : Boolean? = null

fun isLowMemoryDevice(context: Context): Boolean {
    assertMainThread()
    if (isLowMemory == null){
        val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        isLowMemory = manager.isLowRamDevice
    }
    return isLowMemory!!
}