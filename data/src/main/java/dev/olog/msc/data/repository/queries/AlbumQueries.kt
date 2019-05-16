package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway

internal class AlbumQueries constructor(
    prefsGateway: AppPreferencesGateway,
    isPodcast: Boolean,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, isPodcast) {

    fun getAll(chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            GROUP BY $ALBUM_ID
            ORDER BY ${sortOrder()}
            ${tryGetChunk(chunk)}
        """

        return contentResolver.querySql(query)
    }

    fun getById(albumId: Long): Cursor {
        val query = """
            SELECT $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID = ? AND ${defaultSelection()}
            GROUP BY $ALBUM_ID
        """

        return contentResolver.querySql(query, arrayOf(albumId.toString()))
    }

    fun getArtistById(albumId: Long): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID = ? AND ${defaultSelection()}
            GROUP BY $ARTIST_ID
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString()))
    }


    fun getExistingLastPlayed(lastPlayedAlbums: String): Cursor {
        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND $ALBUM_ID in ($lastPlayedAlbums)
            GROUP BY $ALBUM_ID
        """
        return contentResolver.querySql(query)
    }

    fun getRecentlyAdded(chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            GROUP BY $ALBUM_ID
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getSiblings(albumId: Long, chunk: Page?): Cursor {
        val artistQuery = """
             SELECT $ARTIST_ID
             FROM $EXTERNAL_CONTENT_URI
             WHERE $ALBUM_ID = ?
        """
        val cursor = contentResolver.querySql(artistQuery, arrayOf(albumId.toString()))
        cursor.moveToFirst()
        val artistId = cursor.getInt(0)
        cursor.close()

        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ALBUM_ID <> ? AND $ARTIST_ID = ? AND ${defaultSelection()}
            GROUP BY $ALBUM_ID
            ORDER BY $ALBUM_KEY
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString(), artistId.toString()))
    }

    fun getSongList(albumId: Long, chunk: Page?): Cursor {
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
            WHERE ${defaultSongSelection()} AND $ALBUM_ID = ?
            ORDER BY ${songListSortOrder(MediaIdCategory.ALBUMS, DEFAULT_SORT_ORDER)}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString()))
    }

    fun getSongListDuration(albumId: Long): Cursor {
        val query = """
            SELECT sum($DURATION)
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSongSelection()} AND $ALBUM_ID = ?
        """
        return contentResolver.querySql(query, arrayOf(albumId.toString()))
    }

    private fun defaultSelection(): String{
        return "${isPodcast()} AND $ALBUM <> '<unknown>' AND ${notBlacklisted()}"
    }

    private fun defaultSongSelection(): String{
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower(${Columns.ALBUM}) COLLATE UNICODE"
        }

        val (type, arranging) = prefsGateway.getAllAlbumsSortOrder()
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