package dev.olog.msc.shared.ui.theme

interface HasDarkMode {
    fun isWhiteMode(): Boolean
    fun isGrayMode(): Boolean
    fun isDarkMode(): Boolean
    fun isBlackMode(): Boolean

    fun isWhite(): Boolean = isWhiteMode() || isGrayMode()
    fun isDark(): Boolean = isDarkMode() || isBlackMode()
}