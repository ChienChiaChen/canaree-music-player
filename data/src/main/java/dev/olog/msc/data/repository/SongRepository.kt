package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.util.getOrDefault
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.data.repository.util.queryMaybe
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    prefsGateway: AppPreferencesGateway,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserverFlow: ContentObserverFlow

) : SongGateway {

    companion object {
        @JvmStatic
        internal fun updateImages(list: List<Song>, usedImageGateway: UsedImageGateway): List<Song> {
            val allForTracks = usedImageGateway.getAllForTracks()
            val allForAlbums = usedImageGateway.getAllForAlbums()
            if (allForTracks.isEmpty() && allForAlbums.isEmpty()) {
                return list
            }
            return list.map { song ->
                val image = allForTracks.firstOrNull { it.id == song.id }?.image // search for track image
                    ?: allForAlbums.firstOrNull { it.id == song.albumId }?.image  // search for track album image
                    ?: song.image // use default
                song.copy(image = image)
            }
        }

        @JvmStatic
        internal fun adjustImages(context: Context, original: List<Song>): List<Song> {
            val images = CommonQuery.searchForImages(context)
            return original.map { it.copy(image = images.getOrDefault(it.albumId.toInt(), it.image)) }
        }
    }

    private val contentResolver = context.contentResolver
    private val queries = TrackQueries(prefsGateway, false, contentResolver)

    override fun getAll(): DataRequest<Song> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toSong() },
            listMapper = {
                val result = adjustImages(context, it)
                updateImages(result, usedImageGateway)
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<Song> {
        return ItemRequestImpl(
            { queries.getById(param) },
            { it.toSong() },
            {
                val result = adjustImages(context, listOf(it))
                updateImages(result, usedImageGateway).first()
            },
            contentResolver,
            contentObserverFlow,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override suspend fun getByAlbumId(albumId: Long): ItemRequest<Song> {
        return ItemRequestImpl(
            cursorFactory = { queries.getByAlbumId(albumId) },
            cursorMapper = { it.toSong() },
            itemMapper = {
                val result = adjustImages(context, listOf(it))
                updateImages(result, usedImageGateway).first()
            },
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    /**
     * Handles songs and podcast
     */
    override suspend fun getByUri(uri: String): Song? {
        val trackId = getByUriInternal(Uri.parse(uri))?.toLong() ?: return null
        val cursor = queries.getById(trackId, true)
        return contentResolver.queryMaybe(cursor, { it.toSong() }, {
            val result = adjustImages(context, listOf(it))
            updateImages(result, usedImageGateway).first()
        })
    }

    @SuppressLint("Recycle")
    private fun getByUriInternal(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            when (uri.authority) {
                "com.android.providers.media.documents" -> return DocumentsContract.getDocumentId(uri).split(":")[1]
                "media" -> return uri.lastPathSegment
            }
        }
        var songFile: File? = null
        if (uri.authority == "com.android.externalstorage.documents") {
            val child = uri.path?.split(":", limit = 2) ?: listOf()
            songFile = File(Environment.getExternalStorageDirectory(), child[1])
        }

        if (songFile == null) {
            getFilePathFromUri(uri)?.let { path ->
                songFile = File(path)
            }
        }
        if (songFile == null && uri.path != null) {
            songFile = File(uri.path)
        }

        var songId: String? = null

        if (songFile != null) {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(songFile!!.absolutePath),
                null
            )?.let { cursor ->
                cursor.moveToFirst()
                songId = "${cursor.getLong(BaseColumns._ID)}"
                cursor.close()
            }
        }


        return songId
    }

    @SuppressLint("Recycle")
    private fun getFilePathFromUri(uri: Uri): String? {
        var path: String? = null
        context.contentResolver.query(
            uri, arrayOf(MediaStore.Audio.Media.DATA),
            null, null, null
        )?.let { cursor ->
            cursor.moveToFirst()

            path = cursor.getString(MediaStore.Audio.Media.DATA)
            cursor.close()
        }
        return path
    }

    override suspend fun getUneditedByParam(songId: Long): Flow<Song> {
        return TODO()
//        return rxContentResolver.createQuery(
//            TrackQueries.MEDIA_STORE_URI,
//            TrackQueries.PROJECTION,
//            "${MediaStore.Audio.Media._ID} = ?",
//            arrayOf("$songId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1",
//            false
//        ).onlyWithStoragePermission()
//            .debounceFirst()
//            .lift(SqlBrite.Query.mapToOne {
//                val id = it.getLong(BaseColumns._ID)
//                val albumId = it.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
//                val trackImage = usedImageGateway.getForTrack(id)
//                val albumImage = usedImageGateway.getForAlbum(albumId)
//                val image = trackImage ?: albumImage ?: ImagesFolderUtils.forAlbum(context, albumId)
//                it.toUneditedSong(image)
//            }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Song>> {
        return TODO()
//        return rxContentResolver.createQuery(
//            TrackQueries.MEDIA_STORE_URI,
//            TrackQueries.PROJECTION,
//            TrackQueries.SELECTION,
//            null,
//            MediaStore.Audio.Media.DEFAULT_SORT_ORDER,
//            false
//        ).onlyWithStoragePermission()
//            .debounceFirst()
//            .lift(SqlBrite.Query.mapToList { it.toSong() })
//            .doOnError { it.printStackTrace() }
//            .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(songId: Long) {
        assertBackgroundThread()
        // TODO check if works
        assertBackgroundThread()
        val song = getByParam(songId).getItem()
        if (song == null) {
            Log.w("SongRepo", "Song with id=$songId not found")
            return
        }
        val deleted = contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${BaseColumns._ID} = ?",
            arrayOf(songId.toString())
        )
        if (deleted > 0) {
            val file = File(song.path)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    override fun deleteGroup(songList: List<Song>) {
        // TODO check if works
        assertBackgroundThread()

        val idsToDelete = songList.map { "'$it'" }

        val deleted = contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${BaseColumns._ID} IN (${idsToDelete.joinToString()})"
            , null
        )
        if (deleted > 0) {
            val cursor = queries.getExisting(idsToDelete.joinToString())
            val songs = contentResolver.queryAll(cursor, { it.toSong() }, null)
            for (song in songs) {
                val file = File(song.path)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

}

