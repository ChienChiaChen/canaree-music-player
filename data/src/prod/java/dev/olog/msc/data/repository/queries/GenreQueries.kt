package dev.olog.msc.data.repository.queries

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore.Audio.Genres.*
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.CommonQuery.getBlacklistedSelection

internal class GenreQueries {

    companion object {
        internal val MEDIA_STORE_URI = EXTERNAL_CONTENT_URI
        internal const val SELECTION = ""

        internal val PROJECTION_SIMPLE = arrayOf(
            _ID
        )
        internal val PROJECTION = arrayOf(
            _ID,
            NAME
        )
        internal const val SORT_ORDER = "lower($DEFAULT_SORT_ORDER)"
    }

    fun all(context: Context, chunk: ChunkRequest?): Cursor {
        return makeQuery(
            context,
            chunk,
            PROJECTION
        )
    }

    fun size(context: Context): Cursor {
        return makeQuery(
            context,
            null,
            PROJECTION_SIMPLE
        )
    }

    fun genreSize(context: Context, genreId: Long, blackList: Set<String>): Cursor {
        val uri = Members.getContentUri("external", genreId)
        val projection = arrayOf(
            Members._ID,
            Members.AUDIO_ID
        )
        return context.contentResolver.query(
            uri,
            projection,
            getBlacklistedSelection(blackList, "${Members.SIZE} > 0", ""),
            null,
            null
        )!!
    }

    private fun makeQuery(
        context: Context,
        chunk: ChunkRequest?,
        projection: Array<String>
    ): Cursor {

        // TODO filter genres with 0 songs
        return context.contentResolver.query(
            MEDIA_STORE_URI,
            projection,
            SELECTION,
            null,
            if (chunk != null) CommonQuery.makeChunk(
                chunk,
                SORT_ORDER
            ) else SORT_ORDER
        )!!
    }

}