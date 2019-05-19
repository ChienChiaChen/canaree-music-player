package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway

class AlbumQueries constructor(
    prefsGateway: AppPreferencesGateway,
    sortGateway: SortPreferencesGateway,
    isPodcast: Boolean,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, sortGateway, isPodcast) {

    fun getAll(request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $folderProjection as ${Columns.FOLDER},
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} $filter
            GROUP BY $ALBUM_ID
            ORDER BY ${sortOrder()}
            ${tryGetChunk(request?.page)}
        """

        return contentResolver.querySql(query, bindParams)
    }

    fun getById(albumId: Long): Cursor {
        val query = """
            SELECT $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $folderProjection as ${Columns.FOLDER},
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID = ? AND ${defaultSelection()}
            GROUP BY $ALBUM_ID
        """

        return contentResolver.querySql(query, arrayOf(albumId.toString()))
    }

    fun getExistingLastPlayed(lastPlayedAlbums: String): Cursor {
        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $folderProjection as ${Columns.FOLDER},
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND $ALBUM_ID in ($lastPlayedAlbums)
            GROUP BY $ALBUM_ID
        """
        return contentResolver.querySql(query)
    }

    fun getRecentlyAdded(request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $folderProjection as ${Columns.FOLDER},
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()} $filter
            GROUP BY $ALBUM_ID
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, bindParams)
    }

    fun getSiblings(albumId: Long, request: Request?): Cursor {
        val artistQuery = """
             SELECT $ARTIST_ID
             FROM $EXTERNAL_CONTENT_URI
             WHERE $ALBUM_ID = ?
        """
        val cursor = contentResolver.querySql(artistQuery, arrayOf(albumId.toString()))
        cursor.moveToFirst()
        val artistId = cursor.getInt(0)
        cursor.close()

        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $folderProjection as ${Columns.FOLDER},
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID <> ? AND $ARTIST_ID = ? AND ${defaultSelection()}
                $filter
            GROUP BY $ALBUM_ID
            ORDER BY $ALBUM_KEY
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(
            query = query,
            selectionArgs = arrayOf(albumId.toString(), artistId.toString()).plus(bindParams)
        )
    }

    fun getSongList(albumId: Long, request: Request?): Cursor {
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
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSongSelection()} AND $ALBUM_ID = ? $filter
            ORDER BY ${songListSortOrder(MediaIdCategory.ALBUMS, DEFAULT_SORT_ORDER)}
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString()).plus(bindParams))
    }

    fun getSongListDuration(albumId: Long, filterRequest: Filter?): Cursor {
        val (filter, bindParams) = createFilter(
            filterRequest,
            overrideArtistColumn = ARTIST,
            overrideAlbumColumn = ALBUM
        )

        val query = """
            SELECT sum($DURATION)
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSongSelection()} AND $ALBUM_ID = ? $filter
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString()).plus(bindParams))
    }

    private fun defaultSelection(): String {
        return "${isPodcast()} AND $ALBUM <> '<unknown>' AND ${notBlacklisted()}"
    }

    private fun defaultSongSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower(${Columns.ALBUM}) COLLATE UNICODE"
        }

        val (type, arranging) = sortGateway.getAllAlbumsSortOrder()
        var sort = when (type) {
            SortType.ALBUM -> "lower(${Columns.ALBUM})"
            SortType.ARTIST -> "lower(${Columns.ARTIST})"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            else -> "lower(${Columns.ALBUM})"
        }
        if (arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return "$sort COLLATE UNICODE"
    }

}