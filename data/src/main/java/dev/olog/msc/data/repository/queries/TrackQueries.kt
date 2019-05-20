package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway

open class TrackQueries constructor(
    sortGateway: SortPreferencesGateway,
    prefsGateway: AppPreferencesGateway,
    isPodcast: Boolean,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, sortGateway, isPodcast) {

    fun getAll(request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED,
                $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} $filter
            ORDER BY ${sortOrder()}
            ${tryGetChunk(request?.page)}
        """

        return contentResolver.querySql(query, bindParams)
    }

    fun getById(trackId: Long, includeAll: Boolean = false): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $_ID = ? AND ${defaultSelection(includeAll)}
        """

        return contentResolver.querySql(query, selectionArgs = arrayOf(trackId.toString()))
    }

    fun getByAlbumId(albumId: Long): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID = ? AND ${defaultSelection()}
        """

        return contentResolver.querySql(query, selectionArgs = arrayOf(albumId.toString()))
    }

    fun getByLastAdded(request: Request?): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query)
    }

    fun getExisting(songIds: String): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND $_ID in ($songIds)
            ORDER BY $_ID
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(includeAll: Boolean = false): String {
        if (includeAll) {
            return notBlacklisted()
        }
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower($TITLE) COLLATE UNICODE"
        }

        val (type, arranging) = sortGateway.getAllTracksSortOrder()
        var sort = when (type) {
            SortType.TITLE -> "lower($TITLE)"
            SortType.ARTIST -> "lower(${Columns.ARTIST})"
            SortType.ALBUM -> "lower(${Columns.ALBUM})"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            SortType.DURATION -> DURATION
            SortType.RECENTLY_ADDED -> DATE_ADDED
            else -> "lower($TITLE)"
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
}