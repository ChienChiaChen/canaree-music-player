package dev.olog.msc.shared.ui.theme

import android.content.Context

fun Context.playerTheme(): HasPlayerTheme = (applicationContext as HasPlayerTheme)
fun Context.immersive(): HasImmersive = (applicationContext as HasImmersive)
fun Context.miniPlayerTheme(): HasMiniPlayerTheme = (applicationContext as HasMiniPlayerTheme)