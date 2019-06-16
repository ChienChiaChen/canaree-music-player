package dev.olog.msc.data.repository.podcast

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.ApplicationContext
import dev.olog.msc.core.entity.*
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class PlaylistPodcastRepository @Inject constructor(
    @ApplicationContext context: Context,
    appDatabase: AppDatabase,
    private val podcastGateway: PodcastGateway,
    private val favoriteGateway: FavoriteGateway

) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    private fun PodcastPlaylistEntity.toDomain(): PodcastPlaylist {
        return PodcastPlaylist(
            this.id,
            this.name,
            this.size
        )
    }

    override fun getAll(): Observable<List<PodcastPlaylist>> {
        return podcastPlaylistDao.getAllPlaylists()
            .mapToList { it.toDomain() }
            .toObservable()
    }

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int): PodcastPlaylist {
        return PodcastPlaylist(id, title, listSize)
    }

    override fun getAllAutoPlaylists(): Observable<List<PodcastPlaylist>> {
        return Observables.combineLatest(
            podcastGateway.getAll().map { it.count() }.distinctUntilChanged(),
            favoriteGateway.getAllPodcasts().map { it.count() }.distinctUntilChanged(),
            historyDao.getAllPodcasts(podcastGateway.getAll().firstOrError()).map { it.count() }
        ) { last, favorites, history ->
            listOf(
                createAutoPlaylist(AutoPlaylistType.LAST_ADDED.id, autoPlaylistTitles[0], last),
                createAutoPlaylist(AutoPlaylistType.FAVORITE.id, autoPlaylistTitles[1], favorites),
                createAutoPlaylist(AutoPlaylistType.HISTORY.id, autoPlaylistTitles[2], history)
            )
        }
    }

    override fun getByParam(param: Long): Observable<PodcastPlaylist> {
        if (AutoPlaylistType.isAutoPlaylist(param)) {
            return getAllAutoPlaylists().map { it.first { it.id == param } }
        }
        return podcastPlaylistDao.getPlaylist(param)
            .map { it.toDomain() }
            .toObservable()
    }

    override fun observePodcastListByParam(param: Long): Observable<List<Podcast>> {
        return when (param) {
            AutoPlaylistType.LAST_ADDED.id -> getLastAddedSongs()
            AutoPlaylistType.FAVORITE.id -> favoriteGateway.getAllPodcasts()
            AutoPlaylistType.HISTORY.id -> historyDao.getAllPodcasts(podcastGateway.getAll().firstOrError())
            else -> getPlaylistsPodcasts(param)
        }
    }

    private fun getPlaylistsPodcasts(param: Long): Observable<List<Podcast>> {
        return podcastPlaylistDao.getPlaylistTracks(param).toObservable()
            .flatMapSingle { playlistSongs ->
                podcastGateway.getAll().firstOrError().map { songs ->
                    playlistSongs.asSequence()
                        .mapNotNull { playlistSong ->
                            val song = songs.firstOrNull { it.id == playlistSong.podcastId }
                            song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                        }.toList()
                }
            }
    }

    private fun getLastAddedSongs(): Observable<List<Podcast>> {
        return podcastGateway.getAll().switchMapSingle {
            it.toFlowable().toSortedList { o1, o2 -> (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }


    override fun getPlaylistsBlocking(): List<PodcastPlaylist> {
        return podcastPlaylistDao.getAllPlaylistsBlocking()
            .map { it.toDomain() }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable { podcastPlaylistDao.deletePlaylist(playlistId) }
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.fromCallable {
            var maxIdInPlaylist = podcastPlaylistDao.getPlaylistMaxId(playlistId).toLong()
            val tracks = songIds.map {
                PodcastPlaylistTrackEntity(
                    playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                    podcastId = it
                )
            }
            podcastPlaylistDao.insertTracks(tracks)
        }
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.fromCallable {
            podcastPlaylistDao.createPlaylist(
                PodcastPlaylistEntity(name = playlistName, size = 0)
            )
        }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        if (AutoPlaylistType.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                AutoPlaylistType.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
                AutoPlaylistType.HISTORY.id -> return Completable.fromCallable { historyDao.deleteAllPodcasts() }
            }
        }
        return Completable.fromCallable { podcastPlaylistDao.clearPlaylist(playlistId) }
    }

    override fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        if (AutoPlaylistType.isAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return Completable.fromCallable { podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist) }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long): Completable {
        return when (playlistId) {
            AutoPlaylistType.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteType.PODCAST, songId)
            AutoPlaylistType.HISTORY.id -> Completable.fromCallable {
                historyDao.deleteSinglePodcast(
                    songId
                )
            }
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.fromCallable { podcastPlaylistDao.renamePlaylist(playlistId, newTitle) }
    }

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.fromCallable { podcastPlaylistDao.removeDuplicated(playlistId) }
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
        return historyDao.insertPodcasts(podcastId)
    }
}