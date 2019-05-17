package dev.olog.msc.data.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGatewayHelper
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

internal class PlaylistRepositoryHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway

) : PlaylistGatewayHelper {

    private val historyDao = appDatabase.historyDao()

    override fun createPlaylist(playlistName: String): Long {
        val added = System.currentTimeMillis()

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Audio.Playlists.NAME, playlistName)
        contentValues.put(MediaStore.Audio.Playlists.DATE_ADDED, added)
        contentValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED, added)

        val uri = context.contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues)
        return ContentUris.parseId(uri)
    }

    override suspend fun insertSongToHistory(songId: Long) {
        assertBackgroundThread()
        historyDao.insert(songId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        assertBackgroundThread()

        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val cursor = context.contentResolver.query(
            uri, arrayOf("max(${MediaStore.Audio.Playlists.Members.PLAY_ORDER})"),
            null, null, null
        )!!
        cursor.use {
            if (cursor.moveToFirst()) {
                var maxId = cursor.getInt(0) + 1

                val arrayOf = mutableListOf<ContentValues>()
                for (songId in songIds) {
                    val values = ContentValues(2)
                    values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, maxId++)
                    values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId)
                    arrayOf.add(values)
                }

                context.contentResolver.bulkInsert(uri, arrayOf.toTypedArray())
                context.contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
            }
        }
    }

    override fun deletePlaylist(playlistId: Long) {
        assertBackgroundThread()
        context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$playlistId"))
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        assertBackgroundThread()

        if (PlaylistGateway.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                PlaylistGateway.FAVORITE_LIST_ID -> return favoriteGateway.deleteAll(FavoriteType.TRACK)
                PlaylistGateway.HISTORY_LIST_ID -> return historyDao.deleteAll()
            }
        }
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        context.contentResolver.delete(uri, null, null)
    }

    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {
        assertBackgroundThread()

        if (PlaylistGateway.isAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        context.contentResolver.delete(
            uri,
            "${MediaStore.Audio.Playlists.Members._ID} = ?",
            arrayOf("$idInPlaylist")
        )
    }

    private suspend fun removeFromAutoPlaylist(playlistId: Long, songId: Long) {
        return when (playlistId) {
            PlaylistGateway.FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(FavoriteType.TRACK, songId)
            PlaylistGateway.HISTORY_LIST_ID -> historyDao.deleteSingle(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String) {
        assertBackgroundThread()
        val values = ContentValues(1)
        values.put(MediaStore.Audio.Playlists.NAME, newTitle)

        val rowsUpdated = context.contentResolver.update(
            MEDIA_STORE_URI,
            values, "${BaseColumns._ID} = ?", arrayOf("$playlistId")
        )

        if (rowsUpdated < 1){
            Log.w("PlaylistRepo", "Playlist with id $playlistId not renamed")
        }
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return MediaStore.Audio.Playlists.Members.moveItem(context.contentResolver, playlistId, from, to)
    }

    override fun removeDuplicated(playlistId: Long) {
        assertBackgroundThread()

        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val cursor = context.contentResolver.query(
            uri, arrayOf(
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
            ), null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
        )!!
        cursor.use {
            val distinctTrackIds = mutableSetOf<Long>()

            while (cursor.moveToNext()) {
                val trackId = cursor.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
                distinctTrackIds.add(trackId)
            }

            context.contentResolver.delete(uri, null, null)
            addSongsToPlaylist(playlistId, distinctTrackIds.toList())
        }
    }

}