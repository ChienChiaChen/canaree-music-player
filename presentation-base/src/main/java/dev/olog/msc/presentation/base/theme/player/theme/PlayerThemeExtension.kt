package dev.olog.msc.presentation.base.theme.player.theme

import android.content.Context
import dev.olog.msc.shared.ui.theme.HasPlayerTheme

fun Context?.isDefault(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isDefault() else false
}

fun Context?.isFlat(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isFlat() else false
}

fun Context?.isSpotify(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isSpotify() else false
}

fun Context?.isFullscreen(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isFullscreen() else false
}

fun Context?.isBigImage(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isBigImage() else false
}

fun Context?.isClean(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isClean() else false
}

fun Context?.isMini(): Boolean {
    return if(this != null ) (applicationContext as HasPlayerTheme).isMini() else false
}