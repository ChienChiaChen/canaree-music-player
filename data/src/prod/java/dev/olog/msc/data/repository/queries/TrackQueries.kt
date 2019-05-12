package dev.olog.msc.data.repository.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.repository.util.CommonQuery.getBlacklistedSelection
import dev.olog.msc.data.repository.util.CommonQuery.makeChunk
import javax.inject.Inject

internal open class TrackQueries @Inject constructor(
    private val prefsGateway: AppPreferencesGateway,
    private val isPodcast: Boolean
) {

    companion object {
        internal val MEDIA_STORE_URI = EXTERNAL_CONTENT_URI

        val PROJECTION_SIMPLE = arrayOf(_ID)

        internal val PROJECTION = arrayOf(
            _ID,
            ARTIST_ID,
            ALBUM_ID,
            TITLE,
            ARTIST,
            ALBUM,
            DURATION,
            DATA,
            YEAR,
            TRACK,
            DATE_ADDED,
            IS_PODCAST,
            "album_artist"
        )
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

        val sortOrder = makeSortOrder()

        return context.contentResolver.query(
            MEDIA_STORE_URI,
            projection,
            getBlacklistedSelection(blackList, getBaseSelection(), ""),
            null,
            if (chunk != null) makeChunk(
                chunk,
                sortOrder
            ) else sortOrder
        )!!
    }

    private fun getBaseSelection(): String {
        return if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
    }

    private fun makeSortOrder(): String {
        if (isPodcast){
            return DEFAULT_SORT_ORDER
        }

        val (type, arranging) = prefsGateway.getAllTracksSortOrder()
        var sort = when (type) {
            SortType.TITLE -> TITLE_KEY
            SortType.ARTIST -> ARTIST_KEY
            SortType.ALBUM -> ALBUM_KEY
            SortType.ALBUM_ARTIST -> "lower(album_artist)"
            SortType.DURATION -> DURATION
            SortType.RECENTLY_ADDED -> DATE_ADDED
            else -> DEFAULT_SORT_ORDER
        }
        if (arranging == SortArranging.DESCENDING) {
            sort += " DESC"
        }
        return sort
    }

}