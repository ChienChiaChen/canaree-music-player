package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres.*
import android.provider.MediaStore.Audio.Genres.Members.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway

internal class GenreQueries(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, false) {

    fun getAll(chunk: Page?): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Genres._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            ORDER BY ${MediaStore.Audio.Genres.DEFAULT_SORT_ORDER}
            ${tryGetChunk(chunk)}
        """

        return contentResolver.querySql(query)
    }

    fun countAll(): Cursor {
        val query = """
            SELECT count(*)
            FROM $EXTERNAL_CONTENT_URI
        """

        return contentResolver.querySql(query)
    }

    fun countGenreSize(genreId: Long): Cursor {
        // TODO remove genres with 0 tracks if possibile
        val query = """
            SELECT ${Members._ID}, $AUDIO_ID
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getById(genreId: Long): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Genres._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Genres._ID} = ?
        """

        return contentResolver.querySql(query, arrayOf(genreId.toString()))
    }

    fun getSongList(genreId: Long, chunk: Page?): Cursor{
        val query = """
            SELECT ${Members._ID}, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getSongListDuration(genreId: Long): Cursor{
        val query = """
            SELECT sum($DURATION)
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getRecentlyAddedSongs(genreId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT ${Members._ID}, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()}
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getSiblings(genreId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Genres._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Genres._ID} <> ?
            ORDER BY ${MediaStore.Audio.Genres.DEFAULT_SORT_ORDER}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(genreId.toString()))
    }

    fun getRelatedArtists(genreId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()} AND $ARTIST <> '<unknown>'
            GROUP BY $ARTIST_ID
            ORDER BY $ARTIST_KEY
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getExisting(songIds: String): Cursor {
        val query = """
            SELECT ${Members._ID}, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${Members._ID} in ($songIds)
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String{
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

}