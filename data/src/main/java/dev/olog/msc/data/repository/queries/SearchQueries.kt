package dev.olog.msc.data.repository.queries

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import android.util.Log
import dev.olog.contentresolversql.querySql
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.SearchGateway.By
import dev.olog.msc.core.gateway.SearchGateway.SearchRequest
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class SearchQueries @Inject constructor(
    prefsGateway: AppPreferencesGateway,
    private val contentResolver: ContentResolver
) : BaseQueries(
    prefsGateway,
    false // not considered
) {

    enum class SearchType {
        ALL,
        SONGS,
        PODCAST
    }

    fun searchTrack(request: Request?, searchType: SearchType, search: SearchRequest): Cursor {
        val filterSelection = createFilterSelection(search)
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
            WHERE ${defaultSelection(searchType)}
                ${if (filterSelection.isEmpty()) "" else " AND $filterSelection"}
            ORDER BY lower($TITLE) COLLATE UNICODE
            ${tryGetChunk(request?.page)}
        """
        val word = search.byWord.first.trim()
        val numberOfParams = sql.count { it == '?' }
        val bindParams = (0 until numberOfParams).map { "%$word%" }.toTypedArray()
        return contentResolver.querySql(sql, bindParams)
    }

    fun searchTracksInGenre(genre: String): Cursor? {
        val genreQuery = """
            SELECT ${MediaStore.Audio.Genres._ID}
            FROM ${MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI}
            WHERE ${MediaStore.Audio.Genres.NAME} LIKE ?
        """
        val cursor = contentResolver.querySql(genreQuery, arrayOf("%$genre%"))
        if (!cursor.moveToNext()) {
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
            WHERE ${defaultSelection(SearchType.ALL)} AND $_ID = ?
            ORDER BY lower($TITLE) COLLATE UNICODE
        """
        return contentResolver.querySql(sql, arrayOf(genreId.toString()))
    }

    private fun defaultSelection(searchType: SearchType): String {
        return when (searchType) {
            SearchType.ALL -> notBlacklisted()
            SearchType.SONGS -> "$IS_PODCAST = 0 AND ${notBlacklisted()}"
            SearchType.PODCAST -> "$IS_PODCAST <> 0 AND ${notBlacklisted()}"
        }
    }

    private fun createFilterSelection(request: SearchRequest): String {
        var selection = ""
        val (word, columns) = request.byWord

        if (columns.contains(By.NO_FILTER)) {
            By.values().filter { it != By.NO_FILTER }
                .forEach { check(!columns.contains(it)) {
                       "Provide or By.NO_FILTER or one of the others. Current $columns"
                } }
        }

        if (!columns.contains(By.NO_FILTER) && word.isNotBlank()) {
            val conditions = mutableListOf<String>()
            check(columns.isNotEmpty())
            for (column in columns) {
                conditions.add(
                    when (column) {
                        By.TITLE -> "$TITLE LIKE ?"
                        By.ARTIST -> "${Columns.ARTIST} LIKE ?"
                        By.ALBUM -> "${Columns.ALBUM} LIKE ?"
                        else -> throw IllegalArgumentException("invalid condition=by $column")
                    }
                )
            }
            selection = conditions.joinToString(separator = " OR ")
        }
        if (columns.isEmpty()) {
            // like using By.ANY
            Log.w("SearchQueries", "Missing column selection, no filter will be applied")
        }

        if (request.byIds != null && request.byIds!!.isNotEmpty()) {
            val idsSelection = "$_ID IN (${(request.byIds!!.joinToString())})"
            if (selection.isNotBlank()) {
                selection += " AND $idsSelection"
            } else {
                selection = idsSelection
            }
        }

        return selection
    }

}