package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import android.util.Log
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.SearchGateway.By
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class SearchQueries @Inject constructor(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(
    prefsGateway,
    false // not considered
) {

    fun searchTrack(chunk: Page?, word: String, columns: Array<out By>): Cursor {
        val selection = createSelection(word, columns)
        val sql = """
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
            WHERE ${defaultSelection()} AND $selection
            ORDER BY lower($TITLE) COLLATE UNICODE
            ${tryGetChunk(chunk)}
        """
        return contentResolver.querySql(sql, arrayOf("%$word%", "%$word%", "%$word%"))
    }

    fun searchTracksInGenre(genre: String): Cursor? {
        val genreQuery = """
            SELECT ${MediaStore.Audio.Genres._ID}
            FROM ${MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI}
            WHERE ${MediaStore.Audio.Genres.NAME} LIKE ?
        """
        val cursor = contentResolver.querySql(genreQuery, arrayOf("%$genre%"))
        if (!cursor.moveToNext()){
            cursor.close()
            return null
        }
        val genreId = cursor.getLong(0)
        cursor.close()

        val sql = """
            SELECT $_ID, $ARTIST_ID, $ALBUM_ID,
                $TITLE,
                $artistProjection as ${Columns.ARTIST},
                $albumProjection as ${Columns.ALBUM},
                $albumArtistProjection,
                $DURATION, $DATA, $YEAR,
                $discNumberProjection as ${Columns.N_DISC},
                $trackNumberProjection as ${Columns.N_TRACK},
                $DATE_ADDED, $IS_PODCAST
            FROM ${MediaStore.Audio.Genres.Members.getContentUri("external", genreId)}
            WHERE ${defaultSelection()}
            ORDER BY lower($TITLE) COLLATE UNICODE
        """
        return contentResolver.querySql(sql)
    }

    private fun defaultSelection(): String {
        return notBlacklisted()
    }

    private fun createSelection(word: String, columns: Array<out By>): String {
        if (columns.contains(By.ANY)){
            return ""
        }
        if (columns.isEmpty()){
            Log.w("SearchQueries", "Searching $word by not column")
            return ""
        }

        val conditions = mutableListOf<String>()
        for (column in columns) {
            conditions.add(
                when (column) {
                    By.TITLE -> "$TITLE LIKE ?"
                    By.ARTIST -> "${Columns.ARTIST} LIKE ?"
                    By.ALBUM -> "${Columns.ALBUM} LIKE ?"
                    else -> throw IllegalArgumentException("invalid condiction=by $column")
                }
            )
        }
        return conditions.joinToString(separator = " OR ")
    }

}