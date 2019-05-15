package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import androidx.core.util.getOrDefault
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPositionEntity
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    appDatabase: AppDatabase,
    @ApplicationContext private val context: Context,
    private val usedImageGateway: UsedImageGateway,
    private val contentObserver: ContentObserver,
    prefsGateway: AppPreferencesGateway,
    private val contentResolverFlow: ContentResolverFlow

) : PodcastGateway {

    companion object {
        internal fun adjustImages(context: Context, original: List<Podcast>): List<Podcast> {
            // TODO check if update images if missing
            val images = CommonQuery.searchForImages(context)
            return original.map { it.copy(image = images.getOrDefault(it.albumId.toInt(), it.image)) }
        }
    }

    private val contentResolver = context.contentResolver
    private val trackQueries = TrackQueries(prefsGateway, true, contentResolver)

    private val podcastPositionDao = appDatabase.podcastPositionDao()

    override suspend fun getAll(): Flow<List<Podcast>> = flowOf()

    override fun getChunk(): ChunkedData<Podcast> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = trackQueries.getAll(chunkRequest)
                contentResolver.queryAll(cursor, { it.toPodcast() }, { adjustImages(context, it) })
            },
            allDataSize = contentResolver.querySize(trackQueries.countAll()),
            observeChanges = { contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) }
        )
    }

    override fun getByParam(param: Long): Podcast {
        return contentResolver.querySingle(trackQueries.getById(param), { it.toPodcast() }, {
            adjustImages(context, listOf(it)).first()
        })
    }

    override suspend fun observeByParam(param: Long): Flow<Podcast> {
        return contentResolverFlow.createQuery<Podcast>({ trackQueries.getById(param) }, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            .mapToOne { it.toPodcast() }
            .map { adjustImages(context, listOf(it)).first() }
            .distinctUntilChanged()
    }

    override fun getByAlbumId(albumId: Long): Observable<Podcast> {
        return TODO()
//        querySingle("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(albumId.toString()))
//            .asObservable()
    }

    override fun getUneditedByParam(podcastId: Long): Observable<Podcast> {
        return TODO()
//        return rxContentResolver.createQuery(
//            TrackQueries.MEDIA_STORE_URI,
//            TrackQueries.PROJECTION,
//            "${MediaStore.Audio.Media._ID} = ?",
//            arrayOf("$podcastId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
//        ).onlyWithStoragePermission()
//            .debounceFirst()
//            .lift(SqlBrite.Query.mapToOne {
//                val id = it.getLong(BaseColumns._ID)
//                val albumId = it.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
//                val trackImage = usedImageGateway.getForTrack(id)
//                val albumImage = usedImageGateway.getForAlbum(albumId)
//                val image = trackImage ?: albumImage ?: ImagesFolderUtils.forAlbum(context, albumId)
//                it.toUneditedPodcast(image)
//            }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Podcast>> {
        return TODO()
//        return rxContentResolver.createQuery(
//            TrackQueries.MEDIA_STORE_URI,
//            TrackQueries.PROJECTION,
////            SELECTION,
//            "",
//            null,
////            SORT_ORDER,
//            "",
//            false
//        ).onlyWithStoragePermission()
//            .debounceFirst()
//            .lift(SqlBrite.Query.mapToList { it.toPodcast() })
//            .doOnError { it.printStackTrace() }
//            .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(podcastId: Long): Completable = runBlocking {
        //        Single.fromCallable {
//            context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$podcastId"))
//        }
//            .filter { it > 0 }
//            .flatMapSingle { runBlocking { getByParam(podcastId).asObservable().firstOrError() } }
//            .map { File(it.path) }
//            .filter { it.exists() }
//            .map { it.delete() }
//            .toSingle()
//            .ignoreElement()
        Completable.complete()
    }

    override fun deleteGroup(podcastList: List<Podcast>): Completable {
        return Flowable.fromIterable(podcastList)
            .map { it.id }
            .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        podcastPositionDao.setPosition(PodcastPositionEntity(podcastId, position))
    }
}