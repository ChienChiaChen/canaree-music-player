package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.*
import android.provider.MediaStore.Audio.Playlists.Members.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway

internal class PlaylistQueries(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, false) {

    fun getAll(request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter, removeLeadingAnd = true, overrideTitleColumn = NAME)

        val query = """
            SELECT ${MediaStore.Audio.Playlists._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE $filter
            ORDER BY ${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER}
            ${tryGetChunk(request?.page)}
        """

        return contentResolver.querySql(query, bindParams)
    }

    fun countPlaylistSize(playlistId: Long): Cursor {
        // TODO remove playlist with 0 tracks if possibile
        val query = """
            SELECT ${Members._ID}, $AUDIO_ID
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getById(playlistId: Long): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Playlists._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Playlists._ID} = ?
        """

        return contentResolver.querySql(query, arrayOf(playlistId.toString()))
    }

    // _ID is idInPlaylist
    // AUDIO_ID is song id
    fun getSongList(playlistId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT ${Members._ID},
                ${Members.AUDIO_ID}
                $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, ${Members.DATA}, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                ${Members.DATE_ADDED}, $IS_PODCAST
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()} $filter
            ORDER BY ${songListSortOrder(MediaIdCategory.PLAYLISTS, Members.DEFAULT_SORT_ORDER)}
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, bindParams)
    }

    fun getSongListDuration(playlistId: Long, filterRequest: Filter?): Cursor {
        val (filter, bindParams) = createFilter(
            filterRequest,
            overrideArtistColumn = ARTIST,
            overrideAlbumColumn = ALBUM
        )

        val query = """
            SELECT sum($DURATION)
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()} $filter
        """
        return contentResolver.querySql(query, bindParams)
    }

    fun getSiblings(playlistId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter, overrideTitleColumn = NAME)

        val query = """
            SELECT ${MediaStore.Audio.Playlists._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Playlists._ID} <> ? $filter
            ORDER BY ${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER}
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, arrayOf(playlistId.toString()).plus(bindParams))
    }

    fun getRelatedArtists(playlistId: Long, request: Request?): Cursor {
        val (filter, bindParams) = createFilter(request?.filter)

        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()} AND $ARTIST <> '<unknown>' $filter
            GROUP BY $ARTIST_ID
            ORDER BY $ARTIST_KEY
            ${tryGetChunk(request?.page)}
        """
        return contentResolver.querySql(query, bindParams)
    }

    // TODO existing in plylist, not in all
    fun getExisting(songIds: String): Cursor {
        val query = """
            SELECT ${Members._ID}, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, ${Members.DATA}, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                ${Members.DATE_ADDED}, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${Members._ID} in ($songIds)
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String {
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

}