@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.base.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import dev.olog.msc.presentation.base.R
import dev.olog.msc.shared.ui.extensions.themeAttributeToColor
import dev.olog.msc.shared.utils.isOreo

@SuppressLint("NewApi")
@Suppress("DEPRECATION")
fun Context.vibrate(time: Long){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if(isOreo()){
        val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        vibrator.vibrate(time)
    }
}

inline fun Context.scrimColor(): Int {
    return themeAttributeToColor(R.attr.scrimColor)
}