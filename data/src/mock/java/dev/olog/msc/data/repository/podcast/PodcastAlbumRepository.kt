package dev.olog.msc.data.repository.podcast

import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.gateway.PodcastGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class PodcastAlbumRepository @Inject constructor(
    private val podcastGateway: PodcastGateway
) : PodcastAlbumGateway {

    override fun getAll(): Observable<List<PodcastAlbum>> {
        return Observable.just(listOf(
            PodcastAlbum(0, 1, "title", "artist", "", "", 0, false)
        ))
    }

    override fun getAllNewRequest(): Observable<List<PodcastAlbum>> {
        return getAll()
    }

    override fun getByParam(param: Long): Observable<PodcastAlbum> {
        return getAll().map { it.first() }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observePodcastListByParam(albumId: Long): Observable<List<Podcast>> {
        return podcastGateway.getAll()
    }

    override fun observeByArtist(artistId: Long): Observable<List<PodcastAlbum>> {
        return getAll()
    }

    override fun getLastPlayed(): Observable<List<PodcastAlbum>> {
        return getAll()
    }

    override fun addLastPlayed(id: Long): Completable {
        return Completable.complete()
    }
}