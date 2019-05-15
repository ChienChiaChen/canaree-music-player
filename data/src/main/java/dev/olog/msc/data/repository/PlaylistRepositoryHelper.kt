package dev.olog.msc.data.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGatewayHelper
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.utils.getLong
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

internal class PlaylistRepositoryHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway

) : PlaylistGatewayHelper {

    private val historyDao = appDatabase.historyDao()

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.create<Long> { e ->
            val added = System.currentTimeMillis()

            val contentValues = ContentValues()
            contentValues.put(MediaStore.Audio.Playlists.NAME, playlistName)
            contentValues.put(MediaStore.Audio.Playlists.DATE_ADDED, added)
            contentValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED, added)

            try {
                val uri = context.contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues)

                e.onSuccess(ContentUris.parseId(uri))

            } catch (exception: Exception) {
                e.onError(exception)
            }
        }
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return historyDao.insert(songId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) : Completable{
        return Completable.create {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            val cursor = context.contentResolver.query(
                uri, arrayOf("max(${MediaStore.Audio.Playlists.Members.PLAY_ORDER})"),
                null, null, null
            )

            if (cursor != null && cursor.moveToFirst()) {
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
            cursor?.close()

            it.onComplete()
        }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable {
            context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$playlistId"))
        }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        if (PlaylistGateway.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                PlaylistGateway.FAVORITE_LIST_ID -> return favoriteGateway.deleteAll(FavoriteType.TRACK)
                PlaylistGateway.HISTORY_LIST_ID -> return Completable.fromCallable { historyDao.deleteAll() }
            }
        }
        return Completable.fromCallable {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            context.contentResolver.delete(uri, null, null)
        }
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        if (PlaylistGateway.isAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return Completable.fromCallable {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            context.contentResolver.delete(
                uri,
                "${MediaStore.Audio.Playlists.Members._ID} = ?",
                arrayOf("$idInPlaylist")
            )
        }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long): Completable {
        return when (playlistId) {
            PlaylistGateway.FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(FavoriteType.TRACK, songId)
            PlaylistGateway.HISTORY_LIST_ID -> Completable.fromCallable { historyDao.deleteSingle(songId) }
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.create { e ->

            val values = ContentValues(1)
            values.put(MediaStore.Audio.Playlists.NAME, newTitle)

            val rowsUpdated = context.contentResolver.update(
                MEDIA_STORE_URI,
                values, "${BaseColumns._ID} = ?", arrayOf("$playlistId")
            )

            if (rowsUpdated > 0) {
                e.onComplete()
            } else {
                e.onError(Throwable("playlist name not updated"))
            }

        }.subscribeOn(Schedulers.io())
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return MediaStore.Audio.Playlists.Members.moveItem(context.contentResolver, playlistId, from, to)
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.create {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            val cursor = context.contentResolver.query(
                uri, arrayOf(
                    MediaStore.Audio.Playlists.Members._ID,
                    MediaStore.Audio.Playlists.Members.AUDIO_ID
                ), null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
            )

            val distinctTrackIds = mutableSetOf<Long>()

            while (cursor != null && cursor.moveToNext()) {
                val trackId = cursor.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID)
                distinctTrackIds.add(trackId)
            }
            cursor?.close()

            context.contentResolver.delete(uri, null, null)
            addSongsToPlaylist(playlistId, distinctTrackIds.toList())

            it.onComplete()
        }
    }

}