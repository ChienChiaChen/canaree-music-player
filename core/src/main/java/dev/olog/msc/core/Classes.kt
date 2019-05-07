package dev.olog.msc.core

interface Classes {
    fun mainActivity(): Class<*>
    fun musicService(): Class<*>
    fun floatingWindowService(): Class<*>
    fun shortcutActivity(): Class<*>
    fun playlistChooser(): Class<*>
}