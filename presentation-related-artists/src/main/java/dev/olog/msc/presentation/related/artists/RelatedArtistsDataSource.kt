package dev.olog.msc.presentation.related.artists

import android.content.Context
import android.content.res.Resources
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class RelatedArtistsDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val relatedArtistsUseCase: GetRelatedArtistsUseCase,
    private val mediaId: MediaId

) : BaseDataSource<DisplayableItem>() {
    private val resources = context.resources
    private val chunked by lazy { relatedArtistsUseCase.get(mediaId) }

    override fun onAttach() {
        launch {
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
    dataSourceProvider: Provider<RelatedArtistsDataSource>
) : BaseDataSourceFactory<DisplayableItem, RelatedArtistsDataSource>(dataSourceProvider)