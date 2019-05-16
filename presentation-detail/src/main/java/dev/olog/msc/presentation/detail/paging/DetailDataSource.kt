package dev.olog.msc.presentation.detail.paging

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.entity.sort.SortType
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
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.detail.DetailFragmentHeaders
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.RECENTLY_ADDED_VISIBLE_PAGES
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.RELATED_ARTISTS_TO_SEE
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.presentation.detail.domain.GetTotalSongDurationUseCase
import dev.olog.msc.presentation.detail.mapper.toDetailDisplayableItem
import dev.olog.msc.presentation.detail.mapper.toHeaderItem
import dev.olog.msc.shared.ui.TimeUtils
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class DetailDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @FragmentLifecycle lifecycle: Lifecycle,
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
    private val songDurationUseCase: GetTotalSongDurationUseCase

) : BaseDataSource<DisplayableItem>() {

    private val chunked = songListByParamUseCase.execute(mediaId)

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@DetailDataSource) }
            chunked.observeNotification() // TODO check if has to observe sort
                .take(1)
                .collect {
                    invalidate()
                }
        }
        // TODO sort song list in TypeQueries.kt
        // TODO invalidate when updating sort order
        // TODO remove delete button from detail popup
        // TODO invalidate when can show most played
        // TODO invalidate when can show recently added
        // TODO invalidate when can show related artists
        // TODO invalid when siblings change
        // TODO enable filter
    }

    override fun getMainDataSize(): Int {
        return chunked.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf(generateHeader())

        // show sibllings at the top if current item is an artists
        if (mediaId.isArtist && siblingsUseCase.canShow(mediaId)) {
            headers.addAll(displayableHeaders.siblings())
        }
        // most played
        if (mostPlayedUseCase.canShow(mediaId)) {
            headers.addAll(this.displayableHeaders.mostPlayed)
        }
        // recently added
        if (recentlyAddedSongUseCase.canShow(mediaId)) {
            val recentlyAddedSize = recentlyAddedSongUseCase.get(mediaId).getCount()
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

        val duration = songDurationUseCase.execute(mediaId)
        if (duration > 0 && mainListSize > 0) {
            footers.add(createDurationFooter())
        }

        if (relatedArtistsUseCase.canShow(mediaId)) {
            val relatedArtistsSize = relatedArtistsUseCase.get(mediaId).getCount()
            footers.addAll(displayableHeaders.relatedArtists(relatedArtistsSize > RELATED_ARTISTS_TO_SEE))
        }

        if (!mediaId.isArtist && siblingsUseCase.canShow(mediaId)) {
            footers.addAll(displayableHeaders.siblings())
        }
        return footers
    }

    override fun loadInternal(page: Page): List<DisplayableItem> {
        val data = chunked.getPage(page)
        val result = mutableListOf<DisplayableItem>()
        for (datum in data) {
            if (datum is Song) {
                result.add(datum.toDetailDisplayableItem(mediaId, SortType.TITLE)) // TODO sortings
            } else if (datum is Podcast) {
                result.add(datum.toSong().toDetailDisplayableItem(mediaId, SortType.TITLE))
            }
        }
        return result
    }

    private fun createDurationFooter(): DisplayableItem {
        val duration = songDurationUseCase.execute(mediaId)
        val songListSize = chunked.getCount()
        var title = DisplayableItem.handleSongListSize(context.resources, songListSize)
        title += TextUtils.MIDDLE_DOT_SPACED + TimeUtils.formatMillis(context, duration)

        return DisplayableItem(
            R.layout.item_detail_footer, MediaId.headerId("duration footer"), title
        )
    }

    private fun generateHeader(): DisplayableItem {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getByParam(mediaId.categoryValue).getItem()!!.toHeaderItem(resources)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                resources
            )
            MediaIdCategory.ALBUMS -> albumGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem()
            MediaIdCategory.ARTISTS -> artistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(resources)
            MediaIdCategory.GENRES -> genreGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(resources)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                resources
            )
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem()
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getByParam(mediaId.categoryId).getItem()!!.toHeaderItem(
                resources
            )
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}

internal class DetailDataSourceFactory @Inject constructor(
    private val dataSource: Provider<DetailDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}