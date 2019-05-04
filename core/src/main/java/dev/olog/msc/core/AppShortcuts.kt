package dev.olog.msc.core

interface AppShortcuts {

    fun disablePlay()
    fun enablePlay()

    fun addDetailShortcut(mediaId: MediaId, title: String, image: String)

}