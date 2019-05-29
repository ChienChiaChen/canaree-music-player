package dev.olog.msc.shared.ui.theme

import android.content.Context

interface HasImmersive {
    fun isEnabled(): Boolean
}

fun Context.playerTheme(): HasPlayerTheme = (applicationContext as HasPlayerTheme)
fun Context.immersive(): HasImmersive = (applicationContext as HasImmersive)