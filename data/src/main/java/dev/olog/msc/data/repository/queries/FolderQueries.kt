package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway

internal class FolderQueries(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(prefsGateway, false) {


    fun getAll(chunk: ChunkRequest?): Cursor {
        val query = """
            SELECT distinct $folderProjection as ${Columns.FOLDER}, count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
            GROUP BY ${Columns.FOLDER}
            ORDER BY ${sortOrder()}
            ${tryGetChunk(chunk)}
        """

        return contentResolver.querySql(query)
    }

    fun countAll(): Cursor {
        val query = """
            SELECT count(distinct $folderProjection)
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()}
        """
        return contentResolver.querySql(query)
    }

    fun getByPath(folderPath: String): Cursor {
        val query = """
            SELECT distinct $folderProjection as ${Columns.FOLDER}, count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${Columns.FOLDER} = ? AND ${defaultSelection()}
            GROUP BY ${Columns.FOLDER}
        """

        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getSongList(folderPath: String, chunk: ChunkRequest?): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST,
                $folderProjection as ${Columns.FOLDER}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${Columns.FOLDER} = ?
            ORDER BY ${songListSortOrder(MediaIdCategory.FOLDERS, DEFAULT_SORT_ORDER)}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getSongListDuration(folderPath: String): Cursor {
        val query = """
            SELECT sum($DURATION)
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND $folderProjection = ?
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getRecentlyAddedSongs(folderPath: String, chunk: ChunkRequest?): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST,
                $folderProjection as ${Columns.FOLDER}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${isRecentlyAdded()} AND ${Columns.FOLDER} = ?
            GROUP BY ${Columns.FOLDER}
            ORDER BY $DATE_ADDED DESC
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getSiblingsChunk(folderPath: String, chunk: ChunkRequest?): Cursor {
        val query = """
            SELECT distinct $folderProjection as ${Columns.FOLDER}, count(*) as ${Columns.N_SONGS}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${Columns.FOLDER} <> ? AND ${defaultSelection()}
            GROUP BY ${Columns.FOLDER}
            ORDER BY ${sortOrder()}
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getRelatedArtists(folderPath: String, chunk: ChunkRequest?): Cursor {
        val query = """
            SELECT distinct $ARTIST_ID, $ARTIST, $albumArtistProjection,
                count(*) as ${Columns.N_SONGS},
                count(distinct $ALBUM_ID) as ${Columns.N_ALBUMS},
                $folderProjection as ${Columns.FOLDER}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${Columns.FOLDER} = ? AND ${defaultSelection()} AND $ARTIST <> '<unknown>'
            GROUP BY $ARTIST_ID
            ORDER BY $ARTIST_KEY
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    fun getExisting(folderPath: String, songIds: String): Cursor {
        val query = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST,
                $folderProjection as ${Columns.FOLDER}
            FROM $EXTERNAL_CONTENT_URI
            WHERE ${defaultSelection()} AND ${Columns.FOLDER} = ? AND $_ID in ($songIds)
        """
        return contentResolver.querySql(query, arrayOf(folderPath))
    }

    private fun defaultSelection(): String{
        return "${isPodcast()} AND ${notBlacklisted()}"
    }

    private fun sortOrder(): String {
        return "lower(${Columns.FOLDER}) COLLATE UNICODE"
    }


}