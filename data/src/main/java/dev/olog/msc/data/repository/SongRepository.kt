package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.util.getOrDefault
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.*
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.data.utils.getString
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserver: ContentObserver,
    private val contentResolverFlow: ContentResolverFlow

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
    private val trackQueries = TrackQueries(prefsGateway, false, contentResolver)

    private suspend fun queryAll(): Flow<List<Song>> {
        return flowViaChannel { channel ->  // TODO remove
            GlobalScope.launch {
                channel.send(getChunk().chunkOf(ChunkRequest(0, 100)))
            }
        }
    }
    override suspend fun getAll(): Flow<List<Song>> = queryAll()

    override fun getChunk(): ChunkedData<Song> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = trackQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toSong() }, {
                    val result = adjustImages(context, it)
                    updateImages(result, usedImageGateway)
                })
            },
            allDataSize = contentResolver.querySize(trackQueries.countAll()),
            observeChanges = {
                contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    .merge(prefsGateway.observeAllTracksSortOrder().drop(1)) // ignores emission on subscribe
            }
        )
    }

    override fun getByParam(param: Long): Song {
        return contentResolver.querySingle(trackQueries.getById(param), { it.toSong() }, {
            val result = adjustImages(context, listOf(it))
            updateImages(result, usedImageGateway).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<Song> {
        return contentResolverFlow.createQuery<Song>({ trackQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToList { it.toSong() }
            .map {
                val result = adjustImages(context, it)
                updateImages(result, usedImageGateway).first()
            }.distinctUntilChanged()
    }

    override suspend fun getByAlbumId(albumId: Long): Flow<Song> {
//        return querySingle("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(albumId.toString()))
        return TODO()
    }

    @SuppressLint("Recycle")
    override fun getByUri(uri: String): Single<Song> {
        return Single.fromCallable { getByUriInternal(Uri.parse(uri)) }
            .map { it.toLong() }
            .flatMap { runBlocking { Single.just(getByParam(it)) } }
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

    override fun getUneditedByParam(songId: Long): Observable<Song> {
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

    override fun deleteSingle(songId: Long): Completable {
        return Completable.complete()
//        return Single.fromCallable {
//            context.contentResolver.delete(TrackQueries.MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$songId"))
//        }
//            .filter { it > 0 }
//            .flatMapSingle { runBlocking { getByParam(songId).asFlowable().firstOrError() } }
//            .map { File(it.path) }
//            .filter { it.exists() }
//            .map { it.delete() }
//            .toSingle()
//            .ignoreElement()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Flowable.fromIterable(songList)
            .map { it.id }
            .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

}

