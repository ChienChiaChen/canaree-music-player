package dev.olog.msc.presentation.base.theme.dark.mode

import android.content.Context
import dev.olog.msc.shared.ui.theme.HasDarkMode

fun Context?.isWhiteMode(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isWhiteMode() else false
}

fun Context?.isGrayMode(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isGrayMode() else false
}

fun Context?.isDarkMode(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isDarkMode() else false
}

fun Context?.isBlackMode(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isBlackMode() else false
}

fun Context?.isWhite(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isWhite() else false
}

fun Context?.isDark(): Boolean {
    return if(this != null ) (applicationContext as HasDarkMode).isDark() else false
}
