package dev.olog.msc.data.repository.podcast

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class PlaylistPodcastRepository @Inject constructor(
        private val podcastGateway: PodcastGateway

) : PodcastPlaylistGateway {

    private val data = listOf(PodcastPlaylist(
        0, "title", 0, ""
    ))

    override fun getAll(): Observable<List<PodcastPlaylist>> {
        return Observable.just(data)
    }

    override fun getAllAutoPlaylists(): Observable<List<PodcastPlaylist>> {
        return getAll()
    }

    override fun getAllNewRequest(): Observable<List<PodcastPlaylist>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByParam(param: Long): Observable<PodcastPlaylist> {
        return getAll().map { it.first() }
    }

    override fun observePodcastListByParam(param: Long): Observable<List<Podcast>> {
        return podcastGateway.getAll()
    }

    override fun getPlaylistsBlocking(): List<PodcastPlaylist> {
        return data
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.complete()
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.just(0)
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        return Completable.complete()
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.complete()
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertPodcastToHistory(podcastId: Long): Completable {
        return Completable.complete()
    }
}