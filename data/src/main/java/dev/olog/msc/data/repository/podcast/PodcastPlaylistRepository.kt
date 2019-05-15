package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.content.res.Resources
import android.provider.MediaStore
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.core.coroutines.mapToList
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.entity.ChunkedData
import dev.olog.msc.core.entity.favorite.FavoriteType
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.FavoriteGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.data.repository.util.ContentObserver
import dev.olog.msc.data.repository.util.queryAll
import dev.olog.msc.data.repository.util.querySize
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    resources: Resources,
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway,
    prefsKeys: PrefsKeys,
    prefsGateway: AppPreferencesGateway,
    private val contentObserver: ContentObserver
) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    private val contentResolver = context.contentResolver
    private val trackQueries = TrackQueries(prefsGateway, true, contentResolver)

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

    override fun getByParam(param: Long): PodcastPlaylist {
        if (PodcastPlaylistGateway.isPodcastAutoPlaylist(param)) {
            return getAllAutoPlaylists().first { it.id == param }
        }
        return podcastPlaylistDao.getPlaylist(param).toDomain()
    }

    override suspend fun observeByParam(param: Long): Flow<PodcastPlaylist> {
        return podcastPlaylistDao.observeById(param)
            .asFlow()
            .map { it.toDomain() }
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

    override fun getPodcastListByParamChunk(param: Long): ChunkedData<Podcast> {
        return ChunkedData(
            chunkOf = { chunkRequest -> makePodcastListByParamChunk(param, chunkRequest) },
            allDataSize = getPodcastListCountByParam(param),
            observeChanges = observPodcastListByParam(param)
        )
    }

    override fun getPodcastListByParamDuration(param: Long): Int {
        // TODO is needed?
        return when (param) {
            PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID -> 0
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> 0
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> 0
            else -> 0
        }
    }

    override fun getSiblingsChunk(mediaId: MediaId): ChunkedData<PodcastPlaylist> {
        return ChunkedData(
            chunkOf = { chunkRequest ->
                podcastPlaylistDao.getSiblingsChunk(mediaId.categoryId, chunkRequest.limit, chunkRequest.offset)
                    .map { it.toDomain() }
            },
            allDataSize = podcastPlaylistDao.countSiblings(mediaId.categoryId),
            observeChanges = {
                podcastPlaylistDao.observeSibling(mediaId.categoryId)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        )
    }

    override fun canShowSiblings(mediaId: MediaId): Boolean {
        return !PodcastPlaylistGateway.isPodcastAutoPlaylist(mediaId.categoryId) &&
                podcastPlaylistDao.countSiblings(mediaId.categoryId) > 0
    }

    private fun makePodcastListByParamChunk(playlistId: Long, chunkRequest: ChunkRequest): List<Podcast>{
        return when (playlistId) {
            PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID -> {
                contentResolver.queryAll(trackQueries.getByLastAdded(chunkRequest), { it.toPodcast() },
                    { PodcastRepository.adjustImages(context, it) })
            }
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> {
                // TODO sort by now is lost, repair
                val favoritesIds = favoriteGateway.getAllPodcasts(chunkRequest.limit, chunkRequest.offset)
                val existing = contentResolver.queryAll(trackQueries.getExisting(favoritesIds.joinToString { "'$it'" }),
                    { it.toPodcast() }, { PodcastRepository.adjustImages(context, it) })
                favoritesIds.asSequence()
                    .mapNotNull { fav -> existing.first { it.id == fav } }
                    .toList()
            }
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> {
                val historyIds = historyDao.getAllPodcasts(chunkRequest.limit, chunkRequest.offset)
                val existing =
                    contentResolver.queryAll(trackQueries.getExisting(historyIds.joinToString { "'${it.podcastId}'" }),
                        { it.toPodcast() }, { PodcastRepository.adjustImages(context, it) })
                historyIds.asSequence()
                    .mapNotNull { hist -> existing.first { it.id == hist.podcastId } to hist }
                    .map {
                        it.first.copy(
                            trackNumber = it.second.id,
                            dateAdded = it.second.dateAdded
                        )
                    } // TODO check behavior
                    .sortedByDescending { it.dateAdded }
                    .toList()
            }
            else -> {
                val podcastIds = podcastPlaylistDao.getPlaylistTracks(playlistId)
                val existing = contentResolver.queryAll(trackQueries.getExisting(podcastIds.joinToString { "'$it'" }),
                    { it.toPodcast() }, { PodcastRepository.adjustImages(context, it) })
                podcastIds.asSequence()
                    .mapNotNull { podcast ->
                        existing.first { it.id == podcast.podcastId }
                        .copy(trackNumber = podcast.idInPlaylist.toInt())
                    }
                    .toList()
            }
        }
    }

    private fun getPodcastListCountByParam(playlistId: Long): Int {
        return when (playlistId) {
            PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID -> {
                val cursor = trackQueries.countAll()
                contentResolver.querySize(cursor)
            }
            PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.countAll()
            PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> historyDao.countAll()
            else -> podcastPlaylistDao.getCount()
        }
    }

    private fun observPodcastListByParam(playlistId: Long): suspend () -> Flow<Unit> {
        return suspend {
            when (playlistId) {
                PodcastPlaylistGateway.PODCAST_LAST_ADDED_ID -> contentObserver.createQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                PodcastPlaylistGateway.PODCAST_FAVORITE_LIST_ID -> favoriteGateway.observeAllPodcast().asFlow()
                    .drop(1)
                    .map { Unit }
                PodcastPlaylistGateway.PODCAST_HISTORY_LIST_ID -> historyDao.observeAllPodcast().asFlow()
                    .drop(1)
                    .map { Unit }
                else -> podcastPlaylistDao.observePlaylistTracks(playlistId)
                    .asFlow()
                    .drop(1)
                    .map { Unit }
            }
        }
    }

    override fun observePodcastListByParam(param: Long): Observable<List<Podcast>> {
        return Observable.just(emptyList())
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

    override fun insertPodcastToHistory(podcastId: Long): Completable {
        return historyDao.insertPodcasts(podcastId)
    }
}