package dev.olog.msc.musicservice

import androidx.annotation.CheckResult
import androidx.annotation.MainThread
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.PlayingQueueGateway.Companion.MINI_QUEUE_SIZE
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.interactor.item.GetPodcastUseCase
import dev.olog.msc.core.interactor.item.GetSongUseCase
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.musicservice.model.PositionInQueue
import dev.olog.msc.musicservice.model.toMediaEntity
import dev.olog.msc.shared.extensions.swap
import dev.olog.msc.shared.utils.assertBackgroundThread
import dev.olog.msc.shared.utils.assertMainThread
import dev.olog.msc.shared.utils.clamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Contract
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

private const val SKIP_TO_PREVIOUS_THRESHOLD = 10 * 1000 // 10 sec

internal class QueueImpl @Inject constructor(
    private val playingQueueGateway: PlayingQueueGateway,
    private val repeatMode: RepeatMode,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val queueMediaSession: MediaSessionQueue,
    private val getSongUseCase: GetSongUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val enhancedShuffle: EnhancedShuffle
) {

    private val playingQueue = Vector<MediaEntity>()

    private var currentSongPosition by Delegates.observable(-1) { _, _, new ->
        musicPreferencesUseCase.setLastPositionInQueue(new)
    }

    suspend fun updatePlayingQueueAndPersist(songList: List<MediaEntity>) {
        assertBackgroundThread()
        playingQueue.clear()
        playingQueue.addAll(songList)

        persist(songList)
    }

    private suspend fun persist(songList: List<MediaEntity>) = coroutineScope {
        launch(Dispatchers.IO) {
            val save = songList.map { Triple(it.mediaId, it.id, it.idInPlaylist) }
            playingQueueGateway.update(save)
        }
    }

    fun updateCurrentSongPosition(list: List<MediaEntity>, position: Int, immediate: Boolean = false) {
        val copy = list.toList()

        val safePosition = ensurePosition(copy, position)
        val idInPlaylist = copy[safePosition].idInPlaylist
        currentSongPosition = safePosition
        musicPreferencesUseCase.setLastIdInPlaylist(idInPlaylist)

        var miniQueue = copy.asSequence()
            .drop(safePosition + 1)
            .take(MINI_QUEUE_SIZE)
            .toMutableList()
        miniQueue = handleQueueOnRepeatMode(miniQueue)

        val activeId = playingQueue[currentSongPosition].idInPlaylist.toLong()
        val model = MediaSessionQueueModel(activeId, miniQueue)

        if (immediate) {
            queueMediaSession.onNextImmediate(model)
        } else {
            queueMediaSession.onNext(model)
        }
    }

    @CheckResult
    fun getSongById(idInPlaylist: Long): MediaEntity {
        val positionToTest = playingQueue.indexOfFirst { it.idInPlaylist.toLong() == idInPlaylist }
        val safePosition = ensurePosition(playingQueue, positionToTest)
        val media = playingQueue[safePosition]
        updateCurrentSongPosition(playingQueue, safePosition, true)

        return media
    }

    @CheckResult
    @MainThread
    fun getCurrentSong(): MediaEntity? {
        return playingQueue.getOrNull(currentSongPosition)
    }

    @CheckResult
    @MainThread
    fun getNextSong(trackEnded: Boolean): MediaEntity? {
        assertMainThread()

        if (repeatMode.isRepeatOne() && trackEnded) {
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition + 1
        if (newPosition > playingQueue.lastIndex && repeatMode.isRepeatAll()) {
            newPosition = 0
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue, newPosition)
            return media
        }
        return null
    }

    @CheckResult
    @MainThread
    fun getPreviousSong(playerBookmark: Long): MediaEntity? {
        assertMainThread()

        if (/*repeatMode.isRepeatOne() || */playerBookmark > SKIP_TO_PREVIOUS_THRESHOLD) {
            return playingQueue[currentSongPosition]
        }

        var newPosition = currentSongPosition - 1

        if (currentSongPosition == 0 && newPosition < 0 && !repeatMode.isRepeatAll()) {
            // restart song from beginning if is first
            return playingQueue[currentSongPosition]
        }

        if (newPosition < 0 && repeatMode.isRepeatAll()) {
            newPosition = playingQueue.lastIndex
        }

        if (isPositionValid(playingQueue, newPosition)) {
            val media = playingQueue[newPosition]
            updateCurrentSongPosition(playingQueue, newPosition)
            return media
        }
        return null
    }

    @Contract(pure = true)
    @CheckResult
    private fun ensurePosition(list: List<MediaEntity>, position: Int): Int {
        return clamp(position, 0, list.lastIndex)
    }

    @Contract(pure = true)
    @CheckResult
    private fun isPositionValid(list: List<MediaEntity>, position: Int): Boolean {
        return position in 0..list.lastIndex
    }

    suspend fun shuffle() {
        val copy = enhancedShuffle.shuffle(playingQueue)
        playingQueue.clear()
        playingQueue.addAll(copy)

        val currentIdInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val songPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        if (songPosition != 0) {
            playingQueue.swap(0, songPosition)
        }

        updateCurrentSongPosition(playingQueue, 0, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    suspend fun sort() {
        // todo proper sorting in detail
        playingQueue.sortBy { it.idInPlaylist }

        val currentIdInPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentIdInPlaylist }
        updateCurrentSongPosition(playingQueue, newPosition, true)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    fun onRepeatModeChanged() {
        currentSongPosition = ensurePosition(playingQueue, currentSongPosition)
        var list = playingQueue.drop(currentSongPosition + 1).take(MINI_QUEUE_SIZE).toMutableList()
        list = handleQueueOnRepeatMode(list)

        try {
            val activeId = playingQueue[currentSongPosition].idInPlaylist.toLong()
            queueMediaSession.onNext(MediaSessionQueueModel(activeId, list))
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }

    private fun handleQueueOnRepeatMode(list: MutableList<MediaEntity>)
            : MutableList<MediaEntity> {

        val copy = list.toMutableList()

        if (copy.size < MINI_QUEUE_SIZE && repeatMode.isRepeatAll()) {
            while (copy.size <= MINI_QUEUE_SIZE) {
                // add all list for n times
                copy.addAll(playingQueue.take(MINI_QUEUE_SIZE))
            }
            return copy.asSequence().take(MINI_QUEUE_SIZE).toMutableList()
        }
        return copy
    }

    suspend fun handleSwap(from: Int, to: Int) {
        assertMainThread()

        if (from !in 0..playingQueue.lastIndex || to !in 0..playingQueue.lastIndex) {
            return
        }

        playingQueue.swap(from, to)

        val currentInIdPlaylist = musicPreferencesUseCase.getLastIdInPlaylist()
        val newPosition = playingQueue.indexOfFirst { it.idInPlaylist == currentInIdPlaylist }
        updateCurrentSongPosition(playingQueue, newPosition)
        // todo check if current song is first/last ecc and update ui

        persist(playingQueue)
    }

    suspend fun handleSwapRelative(from: Int, to: Int) {
        handleSwap(from + currentSongPosition + 1, to + currentSongPosition + 1)
    }

    suspend fun handleRemove(position: Int): Boolean {
        if (position !in 0..playingQueue.lastIndex) {
            return false
        }

        if (position >= 0 || position < playingQueue.size) {
            // todo case only one song

            playingQueue.removeAt(position)
            if (position <= currentSongPosition) {
                currentSongPosition--
            }
            persist(playingQueue)
        }
        return playingQueue.isEmpty()
    }

    suspend fun handleRemoveRelative(position: Int): Boolean {
        val realPosition = position + currentSongPosition + 1
        return handleRemove(realPosition)
    }

    fun computePositionInQueue(list: List<MediaEntity>, position: Int): PositionInQueue {
        return when {
            repeatMode.isRepeatAll() || repeatMode.isRepeatOne() -> PositionInQueue.IN_MIDDLE
            position == 0 && position == list.lastIndex -> PositionInQueue.BOTH
            position == 0 -> PositionInQueue.FIRST
            position == list.lastIndex -> PositionInQueue.LAST
            else -> PositionInQueue.IN_MIDDLE
        }
    }

    fun currentPositionInQueue(): PositionInQueue {
        return computePositionInQueue(playingQueue, currentSongPosition)
    }

    suspend fun playLater(songIds: List<Long>, isPodcast: Boolean) {
        assertBackgroundThread()
        var maxProgressive = playingQueue.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
        maxProgressive += 1

        val result = mutableListOf<MediaEntity>()
        for (songId in songIds) {
            if (isPodcast) {
                getPodcastUseCase.execute(MediaId.podcastId(songId)).getItem()?.let { podcast ->
                    result.add(podcast.toMediaEntity(maxProgressive++, MediaId.songId(podcast.id)))
                }
            } else {
                getSongUseCase.execute(MediaId.songId(songId)).getItem()?.let { song ->
                    result.add(song.toMediaEntity(maxProgressive++, MediaId.songId(song.id)))
                }
            }
        }


        val copy = playingQueue.toMutableList()
        copy.addAll(result)
        updatePlayingQueueAndPersist(copy)
        onRepeatModeChanged() // not really but updates mini queue
    }

    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean) {
        assertBackgroundThread()
        val before = playingQueue.take(currentSongPosition + 1)
        val after = playingQueue.drop(currentSongPosition + 1)

        val result = mutableListOf<Any>()
        for (songId in songIds) {
            if (isPodcast) {
                getPodcastUseCase.execute(MediaId.podcastId(songId)).getItem()?.let { result.add(it) }
            } else {
                getSongUseCase.execute(MediaId.songId(songId)).getItem()?.let { result.add(it) }
            }
        }

        var currentProgressive = before.maxBy { it.idInPlaylist }?.idInPlaylist ?: -1
        val listToAdd = result.map { item ->
            when (item) {
                is Song -> item.toMediaEntity(currentProgressive++, MediaId.songId(item.id))
                is Podcast -> item.toMediaEntity(currentProgressive++, MediaId.podcastId(item.id))
                else -> throw IllegalArgumentException("nor song nor podcast")
            }
        }
        val afterListUpdated = after.map { it.copy(idInPlaylist = currentProgressive++) }

        val copy = before.asSequence().plus(listToAdd).plus(afterListUpdated).toList()
        updatePlayingQueueAndPersist(copy)
        onRepeatModeChanged() // not really but updates mini queue
    }


}