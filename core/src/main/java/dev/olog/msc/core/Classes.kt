package dev.olog.msc.core

object Classes {

    val mainActivity by lazy { Class.forName("dev.olog.msc.presentation.home.MainActivity")!! }
    val musicService by lazy { Class.forName("dev.olog.msc.musicservice.MusicService")!! }
    val floatingWindowService by lazy { Class.forName("dev.olog.msc.floatingwindowservice.FloatingWindowService")!! }
    val shortcutActivity by lazy { Class.forName("dev.olog.msc.presentation.shortcuts.ShortcutsActivity")!! }
    val playlistChooser by lazy { Class.forName("dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity")!! }

    const val tabFragment = "dev.olog.msc.presentation.tabs.TabFragment"
    const val folderTreeFragment = "dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragment"
}