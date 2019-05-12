package dev.olog.msc.data.repository.queries

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore.Audio.Playlists.*
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.CommonQuery.getBlacklistedSelection

internal class PlaylistQueries {

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

    fun playlistSize(context: Context, playlistId: Long, blackList: Set<String>): Cursor {
        val uri = Members.getContentUri("external", playlistId)
        val projection = arrayOf(
            BaseColumns._ID
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