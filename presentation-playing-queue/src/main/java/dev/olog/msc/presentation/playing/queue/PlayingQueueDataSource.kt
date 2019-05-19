package dev.olog.msc.presentation.playing.queue

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.PlayingQueueSong
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class PlayingQueueDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val playingQueueGateway: PlayingQueueGateway,
    private val musicPrefs: MusicPreferencesGateway

) : BaseDataSource<DisplayableQueueSong>() {

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@PlayingQueueDataSource) }
            playingQueueGateway.observeAll(Page.NO_PAGING).drop(1)
                .merge(musicPrefs.observeLastPositionInQueue().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return playingQueueGateway.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableQueueSong> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableQueueSong> = listOf()

    override fun loadInternal(request: Request): List<DisplayableQueueSong> {
        val currentItemIndex = musicPrefs.getLastPositionInQueue()
        return playingQueueGateway.getAll(request.page)
            .mapIndexed { index, playingQueueSong -> playingQueueSong.toDisplayableItem(index, currentItemIndex) }
    }

    private fun PlayingQueueSong.toDisplayableItem(position: Int, currentItemIndex: Int): DisplayableQueueSong {
        val positionInList = when {
            currentItemIndex == -1 -> "-"
            position > currentItemIndex -> "+${position - currentItemIndex}"
            position < currentItemIndex -> "${position - currentItemIndex}"
            else -> "-"
        }

        return DisplayableQueueSong(
            R.layout.item_playing_queue,
            MediaId.songId(this.idInPlaylist.toLong()),
            title,
            artist,
            positionInList,
            position == currentItemIndex
        )
    }
}

internal class PlayingQueueDataSourceFactory @Inject constructor(
    private val dataSource: Provider<PlayingQueueDataSource>
) : DataSource.Factory<Int, DisplayableQueueSong>() {

    override fun create(): DataSource<Int, DisplayableQueueSong> {
        return dataSource.get()
    }
}