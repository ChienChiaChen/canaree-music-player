package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres.*
import android.provider.MediaStore.Audio.Genres.Members.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway

internal class GenreQueries(
    prefsGateway: AppPreferencesGateway,
    sortGateway: SortPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, sortGateway, false) {

    fun getAll(request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter, removeLeadingAnd = true, overrideTitleColumn = NAME)

        val query = """
            SELECT ${MediaStore.Audio.Genres._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE $filter
            ORDER BY ${MediaStore.Audio.Genres.DEFAULT_SORT_ORDER}
            ${tryGetChunk(request?.page)}
        """

        return contentResolver.querySql(query, bindParams)
    }

    fun countGenreSize(genreId: Long): Cursor {
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

    fun getSongList(genreId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

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
            WHERE ${defaultSelection()} $filter
            ORDER BY ${songListSortOrder(MediaIdCategory.GENRES, Members.DEFAULT_SORT_ORDER)}
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, bindParams)
    }

    fun getSongListDuration(genreId: Long, filterRequest: Filter?): Cursor {
        val (filter, bindParams) = createFilter(
            filterRequest,
            overrideArtistColumn = ARTIST,
            overrideAlbumColumn = ALBUM
        )
        val query = """
            SELECT sum($DURATION)
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()} $filter
        """
        return contentResolver.querySql(query, bindParams)
    }

    fun getRecentlyAddedSongs(genreId: Long, request: Request?): Cursor {
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
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query)
    }

    fun getSiblings(genreId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter, overrideTitleColumn = NAME)

        val query = """
            SELECT ${MediaStore.Audio.Genres._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Genres._ID} <> ? $filter
            ORDER BY ${MediaStore.Audio.Genres.DEFAULT_SORT_ORDER}
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, arrayOf(genreId.toString()).plus(bindParams))
    }

    fun getRelatedArtists(genreId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM ${getContentUri("external", genreId)}
            WHERE ${defaultSelection()} AND $ARTIST <> '<unknown>' $filter
            GROUP BY $ARTIST_ID
            ORDER BY $ARTIST_KEY
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, bindParams)
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

    private fun defaultSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

}