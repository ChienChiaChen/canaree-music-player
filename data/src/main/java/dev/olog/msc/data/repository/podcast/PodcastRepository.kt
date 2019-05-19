package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPositionEntity
import dev.olog.msc.data.entity.custom.ItemRequestImpl
import dev.olog.msc.data.entity.custom.PageRequestImpl
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.ContentObserverFlow
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.Observable
import java.io.File
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    appDatabase: AppDatabase,
    @ApplicationContext private val context: Context,
    prefsGateway: AppPreferencesGateway,
    private val contentObserverFlow: ContentObserverFlow

) : PodcastGateway {

    private val contentResolver = context.contentResolver
    private val queries = TrackQueries(prefsGateway, true, contentResolver)

    private val podcastPositionDao = appDatabase.podcastPositionDao()

    override fun getAll(): DataRequest<Podcast> {
        return PageRequestImpl(
            cursorFactory = { queries.getAll(it) },
            cursorMapper = { it.toPodcast() },
            listMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByParam(param: Long): ItemRequest<Podcast> {
        return ItemRequestImpl(
            cursorFactory = { queries.getById(param) },
            cursorMapper = { it.toPodcast() },
            itemMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    override fun getByAlbumId(albumId: Long): ItemRequest<Podcast> {
        return ItemRequestImpl(
            cursorFactory = { queries.getByAlbumId(albumId) },
            cursorMapper = { it.toPodcast() },
            itemMapper = null,
            contentResolver = contentResolver,
            contentObserverFlow = contentObserverFlow,
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
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

    override fun deleteSingle(podcastId: Long) {
        // TODO check if works
        assertBackgroundThread()
        val podcast = getByParam(podcastId).getItem()
        if (podcast == null) {
            Log.w("PodcastRepo", "Podcast with id=$podcastId not found")
            return
        }
        val deleted = contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${BaseColumns._ID} = ?",
            arrayOf(podcastId.toString())
        )
        if (deleted > 0) {
            val file = File(podcast.path)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    override fun deleteGroup(podcastList: List<Podcast>) {
        // TODO check if works
        assertBackgroundThread()

        val idsToDelete = podcastList.map { "'$it'" }

        val deleted = contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${BaseColumns._ID} IN (${idsToDelete.joinToString()})"
            , null
        )
        if (deleted > 0) {
            val cursor = queries.getExisting(idsToDelete.joinToString())
            val podcasts = contentResolver.queryAll(cursor, { it.toPodcast() }, null)
            for (podcast in podcasts) {
                val file = File(podcast.path)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
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