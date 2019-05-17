package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.*
import android.provider.MediaStore.Audio.Playlists.Members.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway

internal class PlaylistQueries(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, false) {

    fun getAll(chunk: Page?): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Playlists._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            ORDER BY ${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER}
            ${tryGetChunk(chunk)}
        """

        return contentResolver.querySql(query)
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

    fun getSongList(playlistId: Long, chunk: Page?): Cursor{
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
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()}
            ORDER BY ${songListSortOrder(MediaIdCategory.PLAYLISTS, Members.DEFAULT_SORT_ORDER)}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query)
    }

    fun getSongListDuration(playlistId: Long): Cursor{
        val query = """
            SELECT sum($DURATION)
            FROM ${getContentUri("external", playlistId)}
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getSiblings(playlistId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT ${MediaStore.Audio.Playlists._ID}, $NAME
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${MediaStore.Audio.Playlists._ID} <> ?
            ORDER BY ${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(playlistId.toString()))
    }

    fun getRelatedArtists(playlistId: Long, chunk: Page?): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID,
                $artistProjection as ${Columns.ARTIST},
                $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS}
            FROM ${getContentUri("external", playlistId)}
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
                $DURATION, ${Members.DATA}, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                ${Members.DATE_ADDED}, $IS_PODCAST
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${Members._ID} in ($songIds)
        """
        return contentResolver.querySql(query)
    }

    private fun defaultSelection(): String{
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

}