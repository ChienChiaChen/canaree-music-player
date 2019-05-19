package dev.olog.msc.presentation.related.artists

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class RelatedArtistsDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val resources: Resources,
    private val relatedArtistsUseCase: GetRelatedArtistsUseCase,
    private val mediaId: MediaId

) : BaseDataSource<DisplayableItem>() {

    private val chunked by lazy { relatedArtistsUseCase.get(mediaId) }

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@RelatedArtistsDataSource) }
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toRelatedArtist(resources) }
    }

    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
        val albums = if (this.albums == 0) "" else {
            "${resources.getQuantityString(
                R.plurals.common_plurals_album,
                this.albums,
                this.albums
            )}${TextUtils.MIDDLE_DOT_SPACED}"
        }

        return DisplayableItem(
            R.layout.item_related_artist,
            MediaId.artistId(id),
            this.name,
            "$albums$songs"
        )
    }


}

internal class RelatedArtistsDataSourceFactory @Inject constructor(
    private val dataSource: Provider<RelatedArtistsDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}