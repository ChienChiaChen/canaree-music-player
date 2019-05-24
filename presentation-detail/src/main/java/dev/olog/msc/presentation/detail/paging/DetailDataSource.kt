package dev.olog.msc.presentation.detail.paging

import android.content.Context
import android.content.res.Resources
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.core.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.core.interactor.GetSiblingsUseCase
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.played.GetMostPlayedSongsUseCase
import dev.olog.msc.core.interactor.played.GetRecentlyAddedSongsUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.base.utils.handleSongListSize
import dev.olog.msc.presentation.detail.DetailFragmentHeaders
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.RECENTLY_ADDED_VISIBLE_PAGES
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.RELATED_ARTISTS_TO_SEE
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.presentation.detail.domain.GetDetailSortDataUseCase
import dev.olog.msc.presentation.detail.domain.GetTotalSongDurationUseCase
import dev.olog.msc.presentation.detail.domain.ObserveDetailSortDataUseCase
import dev.olog.msc.presentation.detail.mapper.toDetailDisplayableItem
import dev.olog.msc.presentation.detail.mapper.toHeaderItem
import dev.olog.msc.shared.core.flow.merge
import dev.olog.msc.shared.ui.TimeUtils
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class DetailDataSource @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaId: MediaId,
        private val resources: Resources,
        private val displayableHeaders: DetailFragmentHeaders,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val albumGateway: AlbumGateway,
        private val artistGateway: ArtistGateway,
        private val genreGateway: GenreGateway,
        private val podcastPlaylistGateway: PodcastPlaylistGateway,
        private val podcastAlbumGateway: PodcastAlbumGateway,
        private val podcastArtistGateway: PodcastArtistGateway,
        private val mostPlayedUseCase: GetMostPlayedSongsUseCase,
        songListByParamUseCase: GetSongListChunkByParamUseCase,
        private val recentlyAddedSongUseCase: GetRecentlyAddedSongsUseCase,
        private val siblingsUseCase: GetSiblingsUseCase,
        private val relatedArtistsUseCase: GetRelatedArtistsUseCase,
        private val songDurationUseCase: GetTotalSongDurationUseCase,
        private val getSortUseCase: GetDetailSortDataUseCase,
        private val observeSortUseCase: ObserveDetailSortDataUseCase


) : BaseDataSource<DisplayableItem>() {

    private val chunked = songListByParamUseCase.execute(mediaId)

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                    .merge(observeSortUseCase.execute(mediaId).drop(1))
                    .take(1)
                    .collect {
                        invalidate()
                    }
        }
    }

    var filterBy: String = ""
    private val filterRequest by lazy {
        Filter(
                filterBy,
                arrayOf(Filter.By.TITLE, Filter.By.ARTIST, Filter.By.ALBUM)
        )
    }

    private val artistsFilterRequest by lazy { filterRequest.with(byColumn = arrayOf(Filter.By.ARTIST)) }

    override fun getMainDataSize(): Int {
        return chunked.getCount(filterRequest)
    }

    // hide recently added and most played when filter is ON
    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf(generateHeader(mainListSize))

        // show sibllings at the top if current item is an artists
        if (mediaId.isArtist && siblingsUseCase.canShow(mediaId, filterRequest)) {
            headers.addAll(displayableHeaders.siblings())
        }
        // most played
        if (mostPlayedUseCase.canShow(mediaId) && filterBy.isBlank()) {
            headers.addAll(this.displayableHeaders.mostPlayed)
        }
        // recently added
        if (recentlyAddedSongUseCase.canShow(mediaId) && filterBy.isBlank()) {
            val recentlyAddedSize = recentlyAddedSongUseCase.get(mediaId).getCount(Filter.NO_FILTER)
            headers.addAll(
                    this.displayableHeaders.recent(
                            recentlyAddedSize,
                            recentlyAddedSize > RECENTLY_ADDED_VISIBLE_PAGES
                    )
            )
        }

        if (mainListSize != 0) {
            headers.addAll(displayableHeaders.songs)
        } else {
            headers.add(displayableHeaders.no_songs)
        }
        return headers
    }


    override fun getFooters(mainListSize: Int): List<DisplayableItem> {
        val footers = mutableListOf<DisplayableItem>()

        val duration = songDurationUseCase.execute(mediaId, filterRequest)
        if (duration > 0 && mainListSize > 0) {
            footers.add(createDurationFooter(duration))
        }


        if (relatedArtistsUseCase.canShow(mediaId, artistsFilterRequest)) {
            val relatedArtistsSize = relatedArtistsUseCase.get(mediaId).getCount(artistsFilterRequest)
            footers.addAll(displayableHeaders.relatedArtists(relatedArtistsSize > RELATED_ARTISTS_TO_SEE))
        }

        if (!mediaId.isArtist && siblingsUseCase.canShow(mediaId, filterRequest)) {
            footers.addAll(displayableHeaders.siblings())
        }
        return footers
    }

    override fun loadInternal(request: Request): List<DisplayableItem> {
        val sortType = getSortUseCase.execute(mediaId).sortType

        val data = chunked.getPage(request.with(filter = filterRequest))
        val result = mutableListOf<DisplayableItem>()
        for (datum in data) {
            if (datum is Song) {
                result.add(datum.toDetailDisplayableItem(mediaId, sortType))
            } else if (datum is Podcast) {
                result.add(datum.toSong().toDetailDisplayableItem(mediaId, sortType))
            }
        }
        return result
    }

    private fun createDurationFooter(duration: Int): DisplayableItem {
        val songListSize = chunked.getCount(filterRequest)
        var title = handleSongListSize(context.resources, songListSize)
        title += TextUtils.MIDDLE_DOT_SPACED + TimeUtils.formatMillis(context, duration)

        return DisplayableItem(
                R.layout.item_detail_footer, MediaId.headerId("duration footer"), title
        )
    }

    private fun generateHeader(mainListSize: Int): DisplayableItem {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getByParam(mediaId.categoryValue).getItem()!!.toHeaderItem(
                    resources, mainListSize
            )
            MediaIdCategory.PLAYLISTS -> playlistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                    resources, mainListSize
            )
            MediaIdCategory.ALBUMS -> albumGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem()
            MediaIdCategory.ARTISTS -> {
                val siblingsCount = siblingsUseCase.getData(mediaId).getCount(filterRequest)
                artistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(resources, mainListSize, siblingsCount)
            }
            MediaIdCategory.GENRES -> genreGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(resources, mainListSize)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                    resources, mainListSize
            )
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem()
            MediaIdCategory.PODCASTS_ARTISTS -> {
                val siblingsCount = siblingsUseCase.getData(mediaId).getCount(filterRequest)
                podcastArtistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                        resources, mainListSize, siblingsCount
                )
            }
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}

internal class DetailDataSourceFactory @Inject constructor(
        dataSourceProvider: Provider<DetailDataSource>
) : BaseDataSourceFactory<DisplayableItem, DetailDataSource>(dataSourceProvider) {

    private var filterBy: String = ""

    fun updateFilterBy(filterBy: String) {
        if (this.filterBy != filterBy) {
            this.filterBy = filterBy
            dataSource?.invalidate()
        }
    }

    override fun create(): DataSource<Int, DisplayableItem> {
        dataSource?.onDetach()
        dataSource = dataSourceProvider.get()
        dataSource!!.onAttach()
        dataSource!!.filterBy = this.filterBy
        return dataSource!!
    }

    fun invalidate() {
        dataSource?.invalidate()
    }
}