package dev.olog.msc.musicservice

import android.net.Uri
import android.os.Bundle
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.SearchGateway
import dev.olog.msc.core.gateway.SearchGateway.By
import dev.olog.msc.core.gateway.SearchGateway.SearchRequest
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.interactor.GetSongByFileUseCase
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.PodcastPositionUseCase
import dev.olog.msc.core.interactor.played.GetMostPlayedSongsUseCase
import dev.olog.msc.core.interactor.played.GetRecentlyAddedSongsUseCase
import dev.olog.msc.musicservice.interfaces.Queue
import dev.olog.msc.musicservice.model.*
import dev.olog.msc.musicservice.voice.VoiceSearchParams
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import dev.olog.msc.shared.extensions.swap
import dev.olog.msc.shared.utils.clamp
import io.reactivex.functions.Function
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

internal class QueueManager @Inject constructor(
    private val queueImpl: QueueImpl,
    private val queueGateway: PlayingQueueGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val searchGateway: SearchGateway,
    private val shuffleMode: ShuffleMode,
    private val songListUseCase: GetSongListChunkByParamUseCase,
    private val recentlyAddedUseCase: GetRecentlyAddedSongsUseCase,
    private val mostPlayedSongsUseCase: GetMostPlayedSongsUseCase,

    private val getSongByFileUseCase: GetSongByFileUseCase,
    private val enhancedShuffle: EnhancedShuffle,
    private val podcastPosition: PodcastPositionUseCase

) : Queue {

    override suspend fun prepare(): PlayerMediaEntity = coroutineScope {
        val playingQueue = queueGateway.getAll().map { it.toMediaEntity() }
        queueImpl.updatePlayingQueueAndPersist(playingQueue) // TODO why i'm saving at startup? try to remove
        val (queue, index) = lastSessionSong.apply(playingQueue) // TODO change
        queueImpl.updateCurrentSongPosition(queue, index) // TODO why i'm saving at startup?

        val positionInQueue = queueImpl.computePositionInQueue(queue, index)
        val mediaItem = queue[index]
        val bookMark = getLastSessionBookmark(mediaItem)
        mediaItem.toPlayerMediaEntity(positionInQueue, bookMark)
    }

    private fun getLastSessionBookmark(mediaEntity: MediaEntity): Long {
        if (mediaEntity.isPodcast) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            return clamp(bookmark, 0L, mediaEntity.duration)
        } else {
            val bookmark = musicPreferencesUseCase.getBookmark().toInt()
            return clamp(bookmark.toLong(), 0L, mediaEntity.duration)
        }
    }

    private fun getPodcastBookmarkOrDefault(mediaEntity: MediaEntity?, default: Long = 0L): Long {
        if (mediaEntity?.isPodcast == true) {
            val bookmark = podcastPosition.get(mediaEntity.id, mediaEntity.duration)
            return clamp(bookmark, 0L, mediaEntity.duration)
        } else {
            return default
        }
    }

    override suspend fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity {
        val mediaEntity = queueImpl.getSongById(idInPlaylist)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override suspend fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getNextSong(trackEnded)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override suspend fun getPlayingSong(): PlayerMediaEntity {
        val mediaEntity = queueImpl.getCurrentSong()!!
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override suspend fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity? {
        val mediaEntity = queueImpl.getPreviousSong(playerBookmark)
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        return mediaEntity?.toPlayerMediaEntity(queueImpl.currentPositionInQueue(), bookmark)
    }

    override suspend fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): PlayerMediaEntity {
        val songId = mediaId.leaf ?: -1L

        var songList = songListUseCase.execute(mediaId).getAll(Filter.NO_FILTER)
            .map{ any: Any? -> if (any is Podcast){ any.toSong() } else { any as Song } }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        songList = sortOnDemand(songList, extras)
        songList = shuffleIfNeeded(songId).apply(songList)
        queueImpl.updatePlayingQueueAndPersist(songList)
        val (list, position) = getCurrentSongOnPlayFromId(songId).apply(songList)

        queueImpl.updateCurrentSongPosition(list, position)

        val bookmark = getPodcastBookmarkOrDefault(list[position])
        val positionInQueue = queueImpl.computePositionInQueue(list, position)
        return list[position].toPlayerMediaEntity(positionInQueue, bookmark)
    }

    override suspend fun handlePlayFolderTree(mediaId: MediaId): PlayerMediaEntity {
        return handlePlayFromMediaId(mediaId, null)
    }

    private fun sortOnDemand(list: List<MediaEntity>, extras: Bundle?): List<MediaEntity> {
        return try {
            extras!!
            val sortOrder = SortType.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_TYPE)!!)
            val arranging = SortArranging.valueOf(extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING)!!)
            return if (arranging == SortArranging.ASCENDING) {
                list.sortedWith(getAscendingComparator(sortOrder))
            } else {
                list.sortedWith(getDescendingComparator(sortOrder))
            }
        } catch (ex: Exception) {
            list
        }

    }

    override suspend fun handlePlayRecentlyPlayed(mediaId: MediaId): PlayerMediaEntity {
        val songId = mediaId.leaf!!

        var songList = recentlyAddedUseCase.get(mediaId).getAll(Filter.NO_FILTER)
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        songList = shuffleIfNeeded(songId).apply(songList)
        queueImpl.updatePlayingQueueAndPersist(songList)
        val (list, position) = getCurrentSongOnPlayFromId(songId).apply(songList)
        queueImpl.updateCurrentSongPosition(list, position)

        val bookmark = getPodcastBookmarkOrDefault(list[position])
        val positionInQueue = queueImpl.computePositionInQueue(list, position)
        return list[position].toPlayerMediaEntity(positionInQueue, bookmark)
    }

    override suspend fun handlePlayMostPlayed(mediaId: MediaId): PlayerMediaEntity {
        val songId = mediaId.leaf!!

        var songList = mostPlayedSongsUseCase.get(mediaId).getAll(Filter.NO_FILTER)
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        songList = shuffleIfNeeded(songId).apply(songList)
        queueImpl.updatePlayingQueueAndPersist(songList)
        val (list, position) = getCurrentSongOnPlayFromId(songId).apply(songList)
        queueImpl.updateCurrentSongPosition(list, position)

        val bookmark = getPodcastBookmarkOrDefault(list[position])
        val positionInQueue = queueImpl.computePositionInQueue(list, position)
        return list[position].toPlayerMediaEntity(positionInQueue, bookmark)
    }

    override suspend fun handlePlayShuffle(mediaId: MediaId): PlayerMediaEntity {
        shuffleMode.setEnabled(true)
        var songList = songListUseCase.execute(mediaId).getAll(Filter.NO_FILTER)
            .map{ any: Any? -> if (any is Podcast){ any.toSong() } else { any as Song } }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }

        songList = enhancedShuffle.shuffle(songList.toMutableList())
        queueImpl.updatePlayingQueueAndPersist(songList)
        queueImpl.updateCurrentSongPosition(songList, 0)

        val item = songList[0]
        val bookmark = getPodcastBookmarkOrDefault(item)
        val positionInQueue = queueImpl.computePositionInQueue(songList, 0)
        return item.toPlayerMediaEntity(positionInQueue, bookmark)
    }

    override suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity? {
        val song = getSongByFileUseCase.execute(uri.toString()) ?: return null
        val mediaEntity = song.toMediaEntity(0, MediaId.songId(song.id))
        val songList = listOf(mediaEntity)
        queueImpl.updatePlayingQueueAndPersist(songList)
        queueImpl.updateCurrentSongPosition(songList, 0)

        val item = songList[0]
        val bookmark = getPodcastBookmarkOrDefault(mediaEntity)
        val positionInQueue = queueImpl.computePositionInQueue(songList, 0)
        return item.toPlayerMediaEntity(positionInQueue, bookmark)
    }

    override suspend fun handlePlayFromGoogleSearch(query: String, extras: Bundle): PlayerMediaEntity? {

        val params = VoiceSearchParams(query, extras)

        val songList = when {
            // TODO remove search gateway
            params.isAny -> searchGateway.searchSongsAndPocastsBy(SearchRequest(query to arrayOf(By.NO_FILTER))).getAll(Filter.NO_FILTER)
            params.isUnstructured -> searchGateway.searchSongsAndPocastsBy(SearchRequest(query to arrayOf(By.TITLE, By.ARTIST, By.ALBUM))).getAll(Filter.NO_FILTER)
            params.isAlbumFocus -> searchGateway.searchSongsAndPocastsBy(SearchRequest(params.album to arrayOf(By.ALBUM))).getAll(Filter.NO_FILTER)
            params.isArtistFocus -> searchGateway.searchSongsAndPocastsBy(SearchRequest(params.artist to arrayOf(By.ARTIST))).getAll(Filter.NO_FILTER)
            params.isSongFocus -> searchGateway.searchSongsAndPocastsBy(SearchRequest(params.song to arrayOf(By.ARTIST))).getAll(Filter.NO_FILTER)
            params.isGenreFocus -> searchGateway.searchSongsInGenre(params.genre)?.shuffled()
            else -> null
        }?.mapIndexed { index, song -> song.toMediaEntity(index, MediaId.songId(-1)) } ?: return null

        if (songList.isEmpty()){
            return null
        }
        queueImpl.updatePlayingQueueAndPersist(songList)
        queueImpl.updateCurrentSongPosition(songList, 0)

        shuffleMode.setEnabled(false)

        val item = songList[0]
        val bookmark = getPodcastBookmarkOrDefault(item)
        val positionInQueue = queueImpl.computePositionInQueue(songList, 0)
        return item.toPlayerMediaEntity(positionInQueue, bookmark)

    }

    override suspend fun handleSwap(from: Int, to: Int, relative: Boolean) {
        if (relative) {
            queueImpl.handleSwapRelative(from, to)
        } else {
            queueImpl.handleSwap(from, to)
        }
    }

    override suspend fun handleRemove(position: Int, relative: Boolean, callback: (Boolean) -> Unit) {
        if (relative) {
            if (queueImpl.handleRemoveRelative(position)) {
                callback(true)
            }
        } else {
            if (queueImpl.handleRemove(position)) {
                callback(true)
            }
        }
    }

    override suspend fun sort() {
        queueImpl.sort()
    }

    override suspend fun shuffle() {
        queueImpl.shuffle()
    }

    private val lastSessionSong = Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
        val idInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val currentPosition = clamp(list.indexOfFirst { it.idInPlaylist == idInPlaylist }, 0, list.lastIndex)
        Pair(list, currentPosition)
    }

    private fun getCurrentSongOnPlayFromId(songId: Long) =
        Function<List<MediaEntity>, Pair<List<MediaEntity>, Int>> { list ->
            if (shuffleMode.isEnabled() || songId == -1L) {
                Pair(list, 0)
            } else {
                val position = clamp(list.indexOfFirst { it.id == songId }, 0, list.lastIndex)
                Pair(list, position)
            }
        }

    private fun shuffleIfNeeded(songId: Long) = Function<List<MediaEntity>, List<MediaEntity>> { l ->
        var list = l.toList()
        if (shuffleMode.isEnabled()) {
            val item = list.firstOrNull { it.id == songId } ?: l
            list = enhancedShuffle.shuffle(list.toMutableList())
            val songPosition = list.indexOf(item)
            if (songPosition != 0 && songPosition != -1) {
                list.swap(0, songPosition)
            }
        }
        list
    }

    override fun getCurrentPositionInQueue(): PositionInQueue {
        return queueImpl.currentPositionInQueue()
    }

    override fun onRepeatModeChanged() {
        queueImpl.onRepeatModeChanged()
    }

    override suspend fun playLater(songIds: List<Long>, isPodcast: Boolean): PositionInQueue {
        val currentPositionInQueue = getCurrentPositionInQueue()
        queueImpl.playLater(songIds, isPodcast)
        return when (currentPositionInQueue) {
            PositionInQueue.BOTH -> PositionInQueue.FIRST
            PositionInQueue.LAST -> PositionInQueue.IN_MIDDLE
            else -> currentPositionInQueue
        }
    }

    override suspend fun playNext(songIds: List<Long>, isPodcast: Boolean): PositionInQueue {
        val currentPositionInQueue = getCurrentPositionInQueue()
        queueImpl.playNext(songIds, isPodcast)
        return when (currentPositionInQueue) {
            PositionInQueue.BOTH -> PositionInQueue.FIRST
            PositionInQueue.LAST -> PositionInQueue.IN_MIDDLE
            else -> currentPositionInQueue
        }
    }

//    override fun moveToPlayNext(idInPlaylist: Int): PositionInQueue {
//        val currentPositionInQueue = getCurrentPositionInQueue()
//        queueImpl.moveToPlayNext(idInPlaylist)
//        return when (currentPositionInQueue){
//            PositionInQueue.BOTH -> PositionInQueue.FIRST
//            else -> PositionInQueue.IN_MIDDLE
//        }
//    }

    override fun updatePodcastPosition(position: Long) {
        val mediaEntity = queueImpl.getCurrentSong()
        if (mediaEntity?.isPodcast == true) {
            podcastPosition.set(mediaEntity.id, position)
        }
    }
}

private fun getAscendingComparator(sortType: SortType): Comparator<MediaEntity> {
    return when (sortType) {
        SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }
        SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.artist, o2.artist) }
        SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o1.albumArtist, o2.albumArtist) }
        SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o1.album, o2.album) }
        SortType.DURATION -> compareBy { it.duration }
        SortType.RECENTLY_ADDED -> compareBy { it.dateAdded }
        SortType.TRACK_NUMBER -> ComparatorUtils.getMediaEntityAscendingTrackNumberComparator()
        SortType.CUSTOM -> compareBy { 0 }
    }
}

private fun getDescendingComparator(sortType: SortType): Comparator<MediaEntity> {
    return when (sortType) {
        SortType.TITLE -> Comparator { o1, o2 -> collator.safeCompare(o2.title, o1.title) }
        SortType.ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.artist, o1.artist) }
        SortType.ALBUM_ARTIST -> Comparator { o1, o2 -> collator.safeCompare(o2.albumArtist, o1.albumArtist) }
        SortType.ALBUM -> Comparator { o1, o2 -> collator.safeCompare(o2.album, o1.album) }
        SortType.DURATION -> compareByDescending { it.duration }
        SortType.RECENTLY_ADDED -> compareByDescending { it.dateAdded }
        SortType.TRACK_NUMBER -> ComparatorUtils.getMediaEntityDescendingTrackNumberComparator()
        SortType.CUSTOM -> compareByDescending { 0 }
    }
}