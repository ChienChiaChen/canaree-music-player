package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.toAlbum
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.extensions.safeCompare
import dev.olog.msc.shared.onlyWithStoragePermission
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

internal class PodcastAlbumRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        appDatabase: AppDatabase,
        private val rxContentResolver: BriteContentResolver,
        private val podcastGateway: PodcastGateway,
        private val usedImageGateway: UsedImageGateway

) : PodcastAlbumGateway {

    private val lastPlayedDao = appDatabase.lastPlayedPodcastAlbumDao()

    private fun queryAllData(): Observable<List<PodcastAlbum>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
                null, " size ASC LIMIT 1", true
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne { 0 })
                .switchMap { podcastGateway.getAll() }
                .map { songList ->
                    songList.asSequence()
                            .filter { it.album != TrackUtils.UNKNOWN }
                            .distinctBy { it.albumId }
                            .map { song ->
                                song.toAlbum(songList.count { it.albumId == song.albumId })
                            }.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) })
                            .toList()
                }.map { updateImages(it) }
    }

    private fun updateImages(list: List<PodcastAlbum>): List<PodcastAlbum>{
        val allForAlbum = usedImageGateway.getAllForAlbums()
        if (allForAlbum.isEmpty()){
            return list
        }

        return list.map { album ->
            val image = allForAlbum.firstOrNull { it.id == album.id }?.image ?: ImagesFolderUtils.forAlbum(context, album.id)
            album.copy(image = image)
        }
    }


    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<PodcastAlbum>> {
        return cachedData
    }

    override fun getAllNewRequest(): Observable<List<PodcastAlbum>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<PodcastAlbum> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(albumId: Long): Observable<List<Podcast>> {
        return podcastGateway.getAll().map { it.filter { it.albumId == albumId } }
    }

    override fun observeByArtist(artistId: Long): Observable<List<PodcastAlbum>> {
        return getAll().map { it.filter { it.artistId == artistId } }
    }

    override fun getLastPlayed(): Observable<List<PodcastAlbum>> {
        return Observables.combineLatest(
                getAll(),
                lastPlayedDao.getAll().toObservable(),
                { all, lastPlayed ->

                    if (all.size < 5) {
                        listOf() // too few album to show recents
                    } else {
                        lastPlayed.asSequence()
                                .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                                .take(5)
                                .toList()
                    }
                })
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }
}