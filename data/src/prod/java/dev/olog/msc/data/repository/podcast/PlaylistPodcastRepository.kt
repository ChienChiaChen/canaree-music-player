package dev.olog.msc.data.repository.podcast

import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.coroutines.mapToList
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.PodcastGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.shared.extensions.asFlowable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

internal class PlaylistPodcastRepository @Inject constructor(
    resources: Resources,
    appDatabase: AppDatabase,
    private val podcastGateway: PodcastGateway,
    private val favoriteGateway: FavoriteGateway,
    prefsKeys: PrefsKeys

) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    private fun PodcastPlaylistEntity.toDomain(): PodcastPlaylist {
        return PodcastPlaylist(
            this.id,
            this.name,
            this.size,
            ""
        )
    }

    override suspend fun getAll(): Flow<List<PodcastPlaylist>> {
        return podcastPlaylistDao.observeAll().asFlow()
            .mapToList { it.toDomain() }
    }

    private val autoPlaylistTitles = resources.getStringArray(prefsKeys.autoPlaylist())

    private fun createAutoPlaylist(id: Long, title: String): PodcastPlaylist {
        return PodcastPlaylist(id, title, 0, "")
    }

    override fun getAllAutoPlaylists(): List<PodcastPlaylist> {
        return listOf(
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID, autoPlaylistTitles[2])
        )
    }

    override fun getChunk(): ChunkedData<PodcastPlaylist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                podcastPlaylistDao.getChunk(chunkRequest.limit, chunkRequest.offset).map { it.toDomain() }
            },
            allDataSize = podcastPlaylistDao.getCount(),
            observeChanges = {
                podcastPlaylistDao.observeAll()
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override suspend fun getByParam(param: Long): Flow<PodcastPlaylist> {
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(param)) {
            return flowOf(getAllAutoPlaylists()).map { it.first { it.id == param } }
        }
        return podcastPlaylistDao.getPlaylist(param)
            .map { it.toDomain() }
            .asFlow()
    }

    override fun observePodcastListByParam(param: Long): Observable<List<Podcast>> = runBlocking {
        when (param) {
            PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID -> getLastAddedSongs()
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.getAllPodcasts()
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> historyDao.getAllPodcasts(podcastGateway.getAll().asObservable().firstOrError())
            else -> getPlaylistsPodcasts(param)
        }
    }

    private fun getPlaylistsPodcasts(param: Long): Observable<List<Podcast>> = runBlocking {
        podcastPlaylistDao.getPlaylistTracks(param).toObservable()
            .flatMapSingle { playlistSongs ->
                runBlocking {
                    podcastGateway.getAll().asObservable().firstOrError().map { songs ->
                        playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.podcastId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList()
                    }
                }
            }
    }

    private fun getLastAddedSongs(): Observable<List<Podcast>> = runBlocking {
        podcastGateway.getAll().asObservable().switchMapSingle {
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
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(playlistId)) {
            when (playlistId) {
                PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
                PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> return Completable.fromCallable { historyDao.deleteAllPodcasts() }
            }
        }
        return Completable.fromCallable { podcastPlaylistDao.clearPlaylist(playlistId) }
    }

    override fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable {
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return Completable.fromCallable { podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist) }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long): Completable {
        return when (playlistId) {
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(
                FavoriteType.PODCAST,
                songId
            )
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> Completable.fromCallable {
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