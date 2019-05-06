package dev.olog.msc.constants

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.R

object AppConstants {

    private const val TAG = "AppConstants"

    var IGNORE_MEDIA_STORE_COVERS = false

    const val SHORTCUT_SEARCH = "$TAG.shortcut.search"
    const val SHORTCUT_DETAIL = "$TAG.shortcut.detail"
    const val SHORTCUT_DETAIL_MEDIA_ID = "$TAG.shortcut.detail.media.id"
    const val SHORTCUT_PLAYLIST_CHOOSER = "$TAG.shortcut.playlist.chooser"

    const val NO_IMAGE = "NO_IMAGE"

    fun initialize(context: Context){
        updateIgnoreMediaStoreCovers(context)
    }

    fun updateIgnoreMediaStoreCovers(context: Context) {
        IGNORE_MEDIA_STORE_COVERS = getIgnoreMediaStoreCovers(context)
    }

    private fun getIgnoreMediaStoreCovers(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return prefs.getBoolean(context.getString(R.string.prefs_ignore_media_store_cover_key), false)
    }

}