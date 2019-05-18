package dev.olog.msc.presentation.playing.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import javax.inject.Inject


internal class PlayingQueueFragmentViewModel @Inject constructor(
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    dataSourceFactory: PlayingQueueDataSourceFactory

) : ViewModel() {

    fun getCurrentPosition() = musicPreferencesUseCase.getLastPositionInQueue()

    val data: LiveData<PagedList<DisplayableQueueSong>>

    init {
        val pageSize = 30
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(true)
            .build()
        data = LivePagedListBuilder(dataSourceFactory, config).build()
    }
}