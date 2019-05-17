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

internal class ArtistQueries constructor(
    prefsGateway: AppPreferencesGateway,
    isPodcast: Boolean,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, isPodcast) {

    fun getAll(chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            GROUP BY $ARTIST_ID
            ORDER BY ${sortOrder()}
            ${tryGetChunk(chunk)}
        """

        return contentResolver.querySql(query)
    }

    fun getById(artistId: Long): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ARTIST_ID = ? AND ${defaultSelection()}
            GROUP BY $ARTIST_ID
        """

        return contentResolver.querySql(query, arrayOf(artistId.toString()))
    }

    fun getExistingLastPlayed(lastPlayedArtists: String): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND $ARTIST_ID in ($lastPlayedArtists)
            GROUP BY $ARTIST_ID
        """
        return contentResolver.querySql(query)
    }

    fun getRecentlyAdded(chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            GROUP BY $ARTIST_ID
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getSiblings(artistId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ALBUM_ID, $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE $ARTIST_ID = ? AND ${defaultSelection()}
            GROUP BY $ALBUM_ID
            ORDER BY $ALBUM_KEY
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(artistId.toString()))
    }

    fun getSongList(artistId: Long, chunk: Page?): Cursor {
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
            WHERE ${defaultSongSelection()} AND $ARTIST_ID = ?
            ORDER BY ${songListSortOrder(MediaIdCategory.ARTISTS, DEFAULT_SORT_ORDER)}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(artistId.toString()))
    }

    fun getSongListDuration(artistId: Long): Cursor {
        val query = """
            SELECT sum($DURATION)
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSongSelection()} AND $ARTIST_ID = ?
        """
        return contentResolver.querySql(query, arrayOf(artistId.toString()))
    }

    private fun defaultSelection(): String {
        return "${isPodcast()} AND $ARTIST <> '<unknown>' AND ${notBlacklisted()}"
    }

    private fun defaultSongSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        if (isPodcast) {
            return "lower(${Columns.ARTIST}) COLLATE UNICODE"
        }

        val (type, arranging) = prefsGateway.getAllArtistsSortOrder()
        var sort = when (type) {
            SortType.ARTIST -> "lower(${Columns.ARTIST})"
            SortType.ALBUM_ARTIST -> "lower(${Columns.ALBUM_ARTIST})"
            else -> "lower(${Columns.ARTIST})"
        }
        if (arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return "$sort COLLATE UNICODE"
    }

}