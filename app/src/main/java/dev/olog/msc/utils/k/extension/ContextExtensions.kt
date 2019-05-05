package dev.olog.msc.utils.k.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import dev.olog.msc.R
import dev.olog.msc.shared.utils.isOreo
import dev.olog.msc.sharedui.extensions.themeAttributeToColor

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

fun Context.scrimColor(): Int {
    return themeAttributeToColor(R.attr.scrimColor)
}