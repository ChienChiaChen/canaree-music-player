package dev.olog.msc.data.repository.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.Media.*
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.entity.LastPlayedAlbumEntity
import dev.olog.msc.data.repository.util.CommonQuery
import java.util.concurrent.TimeUnit

internal class AlbumQueries constructor(
    private val prefsGateway: AppPreferencesGateway,
    private val isPodcast: Boolean
){

    companion object {
        internal val MEDIA_STORE_URI = TrackQueries.MEDIA_STORE_URI
        private val TWO_WEEKS = TimeUnit.SECONDS.convert(14, TimeUnit.DAYS)

        private val PROJECTION_SIMPLE = arrayOf(
            "distinct $ALBUM_ID"
        )
        internal val PROJECTION = arrayOf(
            "distinct $ALBUM_ID",
            ALBUM,
            ALBUM_ID,
            ARTIST_ID,
            ARTIST,
            "count(*) as songs"
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

    fun existingLastPlayed(context: Context, blackList: Set<String>, onlyAlbums: String): Cursor {

        val selection = CommonQuery.getBlacklistedSelection(
            blackList,
            " $ALBUM_ID in ($onlyAlbums)",
            ") group by ($ALBUM_ID"
        )

        return context.contentResolver.query(
            MEDIA_STORE_URI,
            PROJECTION,
            selection,
            null,
            null
        )!!
    }

    fun recentlyAdded(context: Context, blackList: Set<String>, chunk: ChunkRequest?): Cursor {
        val selection = CommonQuery.getBlacklistedSelection(
            blackList,
            "${getBaseSelection()} AND strftime('%s','now') - $DATE_ADDED <= $TWO_WEEKS",
            ") group by ($ALBUM_ID"
        )

        val sorting = "$DATE_ADDED DESC"
        return context.contentResolver.query(
            MEDIA_STORE_URI,
            PROJECTION,
            selection,
            null,
            if (chunk != null) CommonQuery.makeChunk(
                chunk,
                sorting
            ) else sorting
        )!!
    }

    private fun makeQuery(
        context: Context,
        blackList: Set<String>,
        chunk: ChunkRequest?,
        projection: Array<String>
    ): Cursor {
        val selection = CommonQuery.getBlacklistedSelection(
            blackList,
            getBaseSelection(),
            ") group by ($ALBUM_ID"
        )

        val sortOrder = makeSortOrder()

        return context.contentResolver.query(
            MEDIA_STORE_URI,
            projection,
            selection,
            null,
            if (chunk != null) CommonQuery.makeChunk(
                chunk,
                sortOrder
            ) else sortOrder
        )!!
    }

    private fun getBaseSelection(): String {
        var selection = if (isPodcast) "$IS_PODCAST <> 0" else "$IS_PODCAST = 0"
        selection += " AND $ALBUM <> '<unknown>'"
        return selection
    }

    private fun makeSortOrder(): String{
        if (isPodcast){
            return ALBUM_KEY
        }

        val (type, arranging) = prefsGateway.getAllTracksSortOrder()
        var sort = when (type){
            SortType.TITLE -> ALBUM_KEY
            SortType.ARTIST -> ARTIST_KEY
            SortType.ALBUM_ARTIST -> "lower(album_artist)"
            else -> DEFAULT_SORT_ORDER
        }
        if (arranging == SortArranging.DESCENDING){
            sort += " DESC"
        }
        return sort
    }

}