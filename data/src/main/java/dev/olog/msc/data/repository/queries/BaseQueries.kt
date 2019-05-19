package dev.olog.msc.data.repository.queries

import android.provider.BaseColumns
import android.provider.MediaStore.Audio.Media.*
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.shared.TrackUtils
import java.util.concurrent.TimeUnit


abstract class BaseQueries(
    protected val prefsGateway: AppPreferencesGateway,
    protected val sortGateway: SortPreferencesGateway,
    protected val isPodcast: Boolean
) {

    companion object {
        private val RECENTLY_ADDED_TIME = TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)
    }

    protected fun tryGetChunk(page: Page?): String {
        return """
            ${if (page == null) "" else "LIMIT ${page.limit}"}
            ${if (page == null) "" else "OFFSET ${page.offset}"}
        """.trimIndent()
    }

    /**
     *  Returns the filter selection plus bind params
     *  @param filter when [null] of [Filter.NO_FILTER], it will return the whole data
     *  @param removeLeadingAnd When true, the result will be ' title = ?'.
     *  When false, the result will be ' AND title = ?'
     *  @param overrideTitleColumn some tables, don't uses the [TITLE] column for their title. E.g
     *  [FolderQueries] uses [Columns.FOLDER]
     *
     */
    protected fun createFilter(
        filter: Filter?,
        removeLeadingAnd: Boolean = false,
        overrideTitleColumn: String? = null,
        overrideArtistColumn: String? = null,
        overrideAlbumColumn: String? = null
    ): Pair<String, Array<String>> {
        if (filter == null || filter == Filter.NO_FILTER) {
            return "" to arrayOf()
        }

        val word = filter.word
        if (word.isBlank()) {
            return when (filter.behaviorOnEmpty) {
                Filter.BehaviorOnEmpty.ALL -> "" to arrayOf()
                Filter.BehaviorOnEmpty.NONE -> {
                    if (removeLeadingAnd) {
                        "${BaseColumns._ID} < 0" to arrayOf() // TODO has to work but check
                    } else {
                        " AND ${BaseColumns._ID} < 0" to arrayOf()
                    }
                }
            }
        }
        val columns = filter.byColumn
        check(columns.isNotEmpty())

        val conditions = mutableListOf<String>()
        for (column in columns) {
            var breakRequest = false
            when (column) {
                Filter.By.TITLE -> {
                    if (overrideTitleColumn != null) {
                        conditions.add("$overrideTitleColumn LIKE ?")
                        breakRequest = true
                    } else {
                        conditions.add("$TITLE LIKE ?")
                    }
                }
                Filter.By.ARTIST -> {
                    if (overrideArtistColumn != null) {
                        conditions.add("$overrideArtistColumn LIKE ?")
                    } else {
                        conditions.add("${Columns.ARTIST} LIKE ?")
                    }

                }
                Filter.By.ALBUM -> {
                    if (overrideAlbumColumn != null) {
                        conditions.add("$overrideAlbumColumn LIKE ?")
                    } else {
                        conditions.add("${Columns.ALBUM} LIKE ?")
                    }
                }
            }
            if (breakRequest) {
                break
            }
        }
        val selection = conditions.joinToString(separator = " OR ")
        val bindParams = (0 until conditions.size).map { "%$word%" }.toTypedArray()
        if (removeLeadingAnd) {
            return selection to bindParams
        }
        return " AND $selection" to bindParams
    }

    protected val artistProjection =
        "CASE WHEN $ARTIST = '<unknown>' THEN '${TrackUtils.UNKNOWN_ARTIST}' ELSE $ARTIST END"
    protected val albumProjection =
        "CASE WHEN $ALBUM = '<unknown>' THEN '${TrackUtils.UNKNOWN_ALBUM}' ELSE $ALBUM END"
    protected val albumArtistProjection = "IFNULL(album_artist, $artistProjection) as ${Columns.ALBUM_ARTIST}"
    protected val discNumberProjection = "CASE WHEN $TRACK >= 1000 THEN substr($TRACK, 1, 1) ELSE 0 END"
    protected val trackNumberProjection = "CASE WHEN $TRACK >= 1000 THEN $TRACK % 1000 ELSE $TRACK END"

    protected val folderProjection: String
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

    protected fun songListSortOrder(category: MediaIdCategory, default: String): String {

        val type = getSortType(category)
        val arranging = sortGateway.getDetailSortArranging()
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

        if (type == SortType.CUSTOM) {
            return sort
        }

        sort += " COLLATE UNICODE "

        if (arranging == SortArranging.ASCENDING && type == SortType.RECENTLY_ADDED) {
            // recently added works in reverse
            sort += " DESC"
        }
        if (arranging == SortArranging.DESCENDING) {
            if (type == SortType.RECENTLY_ADDED) {
                // recently added works in reverse
                sort += " ASC"
            } else {
                sort += " DESC"
            }

        }
        return sort
    }

    private fun getSortType(category: MediaIdCategory): SortType {
        return when (category) {
            MediaIdCategory.FOLDERS -> sortGateway.getDetailFolderSortOrder()
            MediaIdCategory.PLAYLISTS -> sortGateway.getDetailPlaylistSortOrder()
            MediaIdCategory.ALBUMS -> sortGateway.getDetailAlbumSortOrder()
            MediaIdCategory.ARTISTS -> sortGateway.getDetailArtistSortOrder()
            MediaIdCategory.GENRES -> sortGateway.getDetailGenreSortOrder()
            else -> throw IllegalArgumentException("invalid category $category")
        }
    }

}