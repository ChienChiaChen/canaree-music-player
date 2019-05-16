package dev.olog.msc.presentation.detail.paging

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.interactor.GetSiblingsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.detail.mapper.toDetailDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class SiblingsDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val mediaId: MediaId,
    private val siblingsUseCase: GetSiblingsUseCase

) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { siblingsUseCase.getChunk(mediaId) }

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@SiblingsDataSource) }
            if (canLoadData) {
                chunked.observeNotification()
                    .take(1)
                    .collect {
                        invalidate()
                    }
            }
        }
    }

    override val canLoadData: Boolean
        get() = siblingsUseCase.canShow(mediaId)

    override fun getMainDataSize(): Int {
        return chunked.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(page: Page): List<DisplayableItem> {
        return chunked.getPage(page)
            .map { mapItems(it!!) }
    }

    private fun mapItems(item: Any): DisplayableItem {
        return when (item) {
            is Folder -> item.toDetailDisplayableItem(resources)
            is Playlist -> item.toDetailDisplayableItem(resources)
            is Album -> item.toDetailDisplayableItem(resources)
            is Genre -> item.toDetailDisplayableItem(resources)
            is PodcastPlaylist -> item.toDetailDisplayableItem(resources)
            is PodcastAlbum -> item.toDetailDisplayableItem(resources)
            else -> throw IllegalArgumentException("item can not be of class=${item::class}")
        }
    }

}

internal class SiblingsDataSourceFactory @Inject constructor(
    private val dataSource: Provider<SiblingsDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}