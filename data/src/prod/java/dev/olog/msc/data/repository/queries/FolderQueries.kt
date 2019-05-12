package dev.olog.msc.data.repository.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.data.repository.util.CommonQuery

internal class FolderQueries {

    companion object {
        internal val MEDIA_STORE_URI = TrackQueries.MEDIA_STORE_URI
        internal const val SELECTION = "$IS_PODCAST = 0"

        private val PROJECTION_SIMPLE = arrayOf(
            "distinct substr($DATA, 1, length($DATA) - length($DISPLAY_NAME) - 1) as folder"
        )

        private val PROJECTION = arrayOf(
            "distinct substr($DATA, 1, length($DATA) - length($DISPLAY_NAME) - 1) as folder",
            "count(*) as songs"
        )

        private const val SORT_ORDER = "lower(folder)"
    }

    fun all(context: Context, blackList: Set<String>, chunk: ChunkRequest?): Cursor {
        return makeQuery(
            context,
            blackList,
            chunk,
            PROJECTION
        )
    }

    fun size(context: Context, blackList: Set<String>): Cursor {
        return makeQuery(
            context,
            blackList,
            null,
            PROJECTION_SIMPLE
        )
    }

    private fun makeQuery(
        context: Context,
        blackList: Set<String>,
        chunk: ChunkRequest?,
        projection: Array<String>
    ): Cursor {
        val selection = CommonQuery.getBlacklistedSelection(
            blackList,
            SELECTION,
            ") group by (folder"
        )
        // TODO filter folders with 0 songs
        return context.contentResolver.query(
            MEDIA_STORE_URI,
            projection,
            selection,
            null,
            if (chunk != null) CommonQuery.makeChunk(
                chunk,
                SORT_ORDER
            ) else SORT_ORDER
        )!!
    }

}