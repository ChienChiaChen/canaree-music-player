package dev.olog.msc.data.repository

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.gateway.RecentSearchesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class RecentSearchesRepository @Inject constructor() : RecentSearchesGateway {

    override fun getAll(): Observable<List<SearchResult>> {
        return Observable.just(listOf(SearchResult(MediaId.playingQueueId, 0, "", "")))
    }

    override fun insertSong(songId: Long): Completable = Completable.complete()
    override fun insertAlbum(albumId: Long): Completable = Completable.complete()
    override fun insertArtist(artistId: Long): Completable = Completable.complete()
    override fun insertPlaylist(playlistId: Long): Completable = Completable.complete()
    override fun insertGenre(genreId: Long): Completable = Completable.complete()
    override fun insertFolder(folderId: Long): Completable = Completable.complete()

    override fun insertPodcast(podcastId: Long): Completable = Completable.complete()
    override fun insertPodcastPlaylist(playlistid: Long): Completable = Completable.complete()
    override fun insertPodcastAlbum(albumId: Long): Completable = Completable.complete()
    override fun insertPodcastArtist(artistId: Long): Completable = Completable.complete()

    override fun deleteSong(itemId: Long): Completable = Completable.complete()
    override fun deleteAlbum(itemId: Long): Completable = Completable.complete()
    override fun deleteArtist(itemId: Long): Completable = Completable.complete()
    override fun deletePlaylist(itemId: Long): Completable = Completable.complete()
    override fun deleteFolder(itemId: Long): Completable = Completable.complete()
    override fun deleteGenre(itemId: Long): Completable = Completable.complete()

    override fun deletePodcast(podcastId: Long): Completable = Completable.complete()
    override fun deletePodcastPlaylist(playlistId: Long): Completable = Completable.complete()
    override fun deletePodcastAlbum(albumId: Long): Completable = Completable.complete()
    override fun deletePodcastArtist(artistId: Long): Completable = Completable.complete()

    override fun deleteAll(): Completable = Completable.complete()
}