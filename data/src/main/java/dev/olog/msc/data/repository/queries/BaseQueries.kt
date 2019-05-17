package dev.olog.msc.data.repository.queries

import android.provider.MediaStore.Audio.Media.*
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.shared.TrackUtils
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit


abstract class BaseQueries(
    protected val prefsGateway: AppPreferencesGateway,
    protected val isPodcast: Boolean
) {

    companion object {
        private val RECENTLY_ADDED_TIME = TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)
    }

    protected fun tryGetChunk(chunk: Page?): String {
        return """
            ${if (chunk == null) "" else "LIMIT ${chunk.limit}"}
            ${if (chunk == null) "" else "OFFSET ${chunk.offset}"}
        """.trimIndent()
    }

    protected val artistProjection = "CASE WHEN $ARTIST = '<unknown>' THEN '${TrackUtils.UNKNOWN_ARTIST}' ELSE $ARTIST END"
    protected val albumProjection = "CASE WHEN $ALBUM = '<unknown>' THEN '${TrackUtils.UNKNOWN_ALBUM}' ELSE $ALBUM END"
    protected val albumArtistProjection = "IFNULL(album_artist, $artistProjection) as ${Columns.ALBUM_ARTIST}"
    protected val discNumberProjection = "CASE WHEN $TRACK >= 1000 THEN substr($TRACK, 1, 1) ELSE 0 END"
    protected val trackNumberProjection = "CASE WHEN $TRACK >= 1000 THEN $TRACK % 1000 ELSE $TRACK END"

    protected val folderProjection : String
        get() = "substr($DATA, 1, length($DATA) - length($DISPLAY_NAME) - 1)"

    protected fun isPodcast(): String {
        return if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
    }

    protected fun isRecentlyAdded(): String {
        return "strftime('%s','now') - $DATE_ADDED <= $RECENTLY_ADDED_TIME"
    }

    protected fun notBlacklisted(): String {
        val blackListed = prefsGateway.getBlackList().map { "'$it'" }
        return "$folderProjection NOT IN (${blackListed.joinToString()})"
    }

    protected fun songListSortOrder(category: MediaIdCategory, default: String): String = runBlocking {

        val type = getSortType(category)
        val arranging = prefsGateway.getSortArranging()
        var sort = when (type) {
            SortType.TITLE -> "lower($TITLE)"
            SortType.ARTIST -> "lower(${Columns.ARTIST})"
            SortType.ALBUM -> "lower(${Columns.ALBUM})"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            SortType.RECENTLY_ADDED -> DATE_ADDED // DESC
            SortType.DURATION -> DURATION
            SortType.TRACK_NUMBER -> "${Columns.N_DISC}, ${Columns.N_TRACK}, $TITLE"
            SortType.CUSTOM -> default
            else -> "lower($TITLE)"
        }

        if (type == SortType.CUSTOM){
            return@runBlocking sort
        }

        sort += " COLLATE UNICODE "

        if (arranging == SortArranging.ASCENDING && type == SortType.RECENTLY_ADDED){
            // recently added works in reverse
            sort += " DESC"
        }
        if (arranging == SortArranging.DESCENDING) {
            if (type == SortType.RECENTLY_ADDED){
                // recently added works in reverse
                sort += " ASC"
            } else {
                sort += " DESC"
            }

        }
        return@runBlocking sort
    }

    private fun getSortType(category: MediaIdCategory): SortType {
        return when (category){
            MediaIdCategory.FOLDERS -> prefsGateway.getFolderSortOrder()
            MediaIdCategory.PLAYLISTS -> prefsGateway.getPlaylistSortOrder()
            MediaIdCategory.ALBUMS -> prefsGateway.getAlbumSortOrder()
            MediaIdCategory.ARTISTS -> prefsGateway.getArtistSortOrder()
            MediaIdCategory.GENRES -> prefsGateway.getGenreSortOrder()
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

}