package dev.olog.msc.shared.ui.theme

interface HasPlayerTheme {
    fun isDefault(): Boolean
    fun isFlat(): Boolean
    fun isSpotify(): Boolean
    fun isFullscreen(): Boolean
    fun isBigImage(): Boolean
    fun isClean(): Boolean
    fun isMini(): Boolean
}