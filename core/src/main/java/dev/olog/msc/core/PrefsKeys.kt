package dev.olog.msc.core

interface PrefsKeys {
    fun defaultBottomNavigationPage(): Int
    fun defaultDetailSections(): List<Int>
    fun defaultColorAccent(): Int
    fun defaultQuickAction(): Int
    fun defaultAutoDownloadImages(): Int
    fun defaultPlayerAppearance(): Int
    fun defaultDarkMode(): Int
    fun defaultVisibleDetailSections(): Int

    fun visibleDetailSections(): Int
    fun playerControlsVisibility(): Int
    fun showLockscreenArtwork(): Int
    fun adaptiveColors(): Int
    fun showPodcast(): Int
    fun showNewAlbumsArtists(): Int
    fun showRecentAlbumsArtists(): Int
    fun colorAccent(): Int
    fun autoCreateImages(): Int
    fun autoDownloadImages(): Int
    fun showFoldersAsTree(): Int
    fun quickAction(): Int
    fun playerAppearance(): Int
    fun darkMode(): Int
    fun usedEqualizer(): Int

    fun autoDownloadImageNever(): Int
    fun autoDownloadImageWifiOnly(): Int
    fun autoDownloadImageAlways(): Int

    fun showFolderAsTreeView(): Int

    fun autoPlaylist(): Int

    // music
    fun midnightMode(): Int

    fun crossfade(): Int
    fun gapless(): Int
}