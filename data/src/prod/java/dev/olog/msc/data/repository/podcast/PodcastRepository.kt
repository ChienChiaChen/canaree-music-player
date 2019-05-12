package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.util.getOrDefault
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.core.coroutines.debounceFirst
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPositionEntity
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toUneditedPodcast
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.ContentResolverFlow
import dev.olog.msc.data.utils.getLong
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.onlyWithStoragePermission
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import java.io.File
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    appDatabase: AppDatabase,
    @ApplicationContext private val context: Context,
    private val rxContentResolver: BriteContentResolver,
    private val usedImageGateway: UsedImageGateway,
    private val contentResolver: ContentResolverFlow,
    private val contentObserver: ContentObserver,
    private val prefsGateway: AppPreferencesGateway

) : PodcastGateway {

    private val trackQueries = TrackQueries(prefsGateway, true)

    private val podcastPositionDao = appDatabase.podcastPositionDao()

    private suspend fun queryAll(): Flow<List<Podcast>> {
        return flowOf()
//        return contentResolver.createQuery<Podcast>(
//            MEDIA_STORE_URI, PROJECTION, SELECTION,
//            null, SORT_ORDER, true
//        ).mapToList { it.toPodcast() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .distinctUntilChanged()
    }

    private suspend fun querySingle(selection: String, args: Array<String>): Flow<Podcast> {
        return flow { }
//        return contentResolver.createQuery<Podcast>(
//            MEDIA_STORE_URI, PROJECTION, selection,
//            args, SORT_ORDER, true
//        ).mapToList { it.toPodcast() }
//            .emitOnlyWithStoragePermission()
//            .adjust()
//            .map { it.first() }
//            .distinctUntilChanged()
    }

    override fun getChunk(): ChunkedData<Podcast> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                val cursor = trackQueries.all(context, prefsGateway.getBlackList(), chunkRequest)
                CommonQuery.query(cursor, { it.toPodcast() }, { adjustImages(it) })
            },
            allDataSize = CommonQuery.sizeQuery(trackQueries.size(context, prefsGateway.getBlackList())),
            observeChanges = { contentObserver.createQuery(TrackQueries.MEDIA_STORE_URI) }
        )
    }

    private fun adjustImages(original: List<Podcast>): List<Podcast> {
        val images = CommonQuery.searchForImages(context)
        return original.map { it.copy(image = images.getOrDefault(it.albumId.toInt(), it.image)) }
    }

    override suspend fun getAll(): Flow<List<Podcast>> = queryAll()

    override suspend fun getByParam(param: Long): Flow<Podcast> {
        return querySingle("${MediaStore.Audio.Media._ID} = ?", arrayOf(param.toString()))
    }

    override fun getByAlbumId(albumId: Long): Observable<Podcast> = runBlocking {
        querySingle("${MediaStore.Audio.Media.ALBUM_ID} = ?", arrayOf(albumId.toString()))
            .asObservable()
    }

    override fun getUneditedByParam(podcastId: Long): Observable<Podcast> {
        return rxContentResolver.createQuery(
            TrackQueries.MEDIA_STORE_URI,
            TrackQueries.PROJECTION,
            "${MediaStore.Audio.Media._ID} = ?",
            arrayOf("$podcastId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
        ).onlyWithStoragePermission()
            .debounceFirst()
            .lift(SqlBrite.Query.mapToOne {
                val id = it.getLong(BaseColumns._ID)
                val albumId = it.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
                val trackImage = usedImageGateway.getForTrack(id)
                val albumImage = usedImageGateway.getForAlbum(albumId)
                val image = trackImage ?: albumImage ?: ImagesFolderUtils.forAlbum(context, albumId)
                it.toUneditedPodcast(image)
            }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Podcast>> {
        return rxContentResolver.createQuery(
            TrackQueries.MEDIA_STORE_URI,
            TrackQueries.PROJECTION,
//            SELECTION,
            "",
            null,
//            SORT_ORDER,
            "",
            false
        ).onlyWithStoragePermission()
            .debounceFirst()
            .lift(SqlBrite.Query.mapToList { it.toPodcast() })
            .doOnError { it.printStackTrace() }
            .onErrorReturnItem(listOf())
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