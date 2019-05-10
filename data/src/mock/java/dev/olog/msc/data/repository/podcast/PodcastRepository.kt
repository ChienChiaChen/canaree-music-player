package dev.olog.msc.data.repository.podcast

import android.content.Context
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.data.db.AppDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
        appDatabase: AppDatabase,
        @ApplicationContext private val context: Context,
        private  val rxContentResolver: BriteContentResolver,
        private  val usedImageGateway: UsedImageGateway

): PodcastGateway {

    override fun getAll(): Observable<List<Podcast>> = Observable.just(listOf(
        Podcast(0, 1, 2, "title", "artist", "", "album", "",
            0, 0, "", "", 0, 0)
    ))

    override fun getAllNewRequest(): Observable<List<Podcast>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<Podcast> {
        return getAll().map { it.first() }
    }

    override fun getByAlbumId(albumId: Long): Observable<Podcast> {
        return getByParam(albumId)
    }

    override fun getUneditedByParam(podcastId: Long): Observable<Podcast> {
        return getByParam(podcastId)
    }

    override fun getAllUnfiltered(): Observable<List<Podcast>> {
        return getAll()
    }

    override fun deleteSingle(podcastId: Long): Completable {
        return Completable.complete()

    }

    override fun deleteGroup(podcastList: List<Podcast>): Completable {
        return Completable.complete()
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        return 0
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {

    }
}