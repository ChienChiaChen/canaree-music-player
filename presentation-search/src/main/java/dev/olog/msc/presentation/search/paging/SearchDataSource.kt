package dev.olog.msc.presentation.search.paging

import android.content.Context
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.SearchFilters
import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.search.R
import dev.olog.msc.presentation.search.SearchFragmentHeaders
import dev.olog.msc.shared.RecentSearchesTypes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class SearchDataSource @Inject constructor(
        @ApplicationContext private val context: Context,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway,

        private val podcastAlbumGateway: PodcastAlbumGateway,
        private val albumGateway: AlbumGateway,

        private val podcastArtistGateway: PodcastArtistGateway,
        private val artistGateway: ArtistGateway,

        private val genreGateway: GenreGateway,
        private val folderGateway: FolderGateway,

        private val podcastPlaylistGateway: PodcastPlaylistGateway,
        private val playlistGateway: PlaylistGateway,

        private val displayableHeaders: SearchFragmentHeaders,
        private val recentSearchesGateway: RecentSearchesGateway,
        prefsGateway: AppPreferencesGateway
) : BaseDataSource<DisplayableItem>() {

    var filterBy: String = ""

    private val filters = prefsGateway.getSearchFilters()
    private val podcastsOnly = filters.contains(SearchFilters.PODCAST)

    override fun onAttach() {
        launch {
            if (podcastsOnly){
                podcastGateway.getAll().observeNotification()
                        .collect { invalidate() }
            } else {
                songGateway.getAll().observeNotification()
                        .collect { invalidate() }
            }
        }
    }

    private val filterRequest by lazy {
        Filter(
                filterBy,
                arrayOf(Filter.By.TITLE, Filter.By.ARTIST, Filter.By.ALBUM),
                Filter.BehaviorOnEmpty.NONE
        )
    }

    override fun getMainDataSize(): Int {
        if (podcastsOnly) {
            return podcastGateway.getAll().getCount(filterRequest)
        }
        return songGateway.getAll().getCount(filterRequest)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        if (mainListSize == 0) {
            val headers = recentSearchesGateway.getAll().map { it.toSearchDisplayableItem(context) }.toMutableList()
            if (headers.isNotEmpty()) {
                headers.add(DisplayableItem(R.layout.item_search_clear_recent, MediaId.headerId("clear recent"), ""))
                headers.addAll(0, displayableHeaders.recents)
            }
            return headers
        } else {
            val headers = mutableListOf<DisplayableItem>()

            val albumsSize = if (podcastsOnly) podcastAlbumGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ALBUM, Filter.By.ARTIST)))
            else albumGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ALBUM, Filter.By.ARTIST)))

            val artistsSize = if (podcastsOnly) podcastArtistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ARTIST)))
            else artistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ARTIST)))

            val playlistsSize = if (podcastsOnly) podcastPlaylistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))
            else playlistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))

            val foldersSize = folderGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))
            val genresSize = genreGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))

            val songsSize = if (podcastsOnly) podcastGateway.getAll().getCount(filterRequest)
            else songGateway.getAll().getCount(filterRequest)

            if (albumsSize > 0 && filters.contains(SearchFilters.ALBUM)) {
                headers.addAll(displayableHeaders.albumsHeaders(albumsSize))
            }
            if (artistsSize > 0 && filters.contains(SearchFilters.ARTIST)) {
                headers.addAll(displayableHeaders.artistsHeaders(artistsSize))
            }
            if (playlistsSize > 0 && filters.contains(SearchFilters.PLAYLIST)) {
                headers.addAll(displayableHeaders.playlistsHeaders(playlistsSize))
            }
            if (foldersSize > 0 && filters.contains(SearchFilters.FOLDER)) {
                headers.addAll(displayableHeaders.foldersHeaders(foldersSize))
            }
            if (genresSize > 0 && filters.contains(SearchFilters.GENRE)) {
                headers.addAll(displayableHeaders.genreHeaders(genresSize))
            }
            if (songsSize > 0) {
                headers.add(displayableHeaders.songsHeaders(songsSize))
            }
            return headers
        }
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        if (podcastsOnly) {
            return podcastGateway.getAll().getPage(request.with(filter = filterRequest))
                    .map { it.toSearchDisplayableItem() }
        }
        return songGateway.getAll().getPage(request.with(filter = filterRequest))
                .map { it.toSearchDisplayableItem() }
    }

    private fun Song.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_search_song,
                MediaId.songId(this.id),
                title,
                artist,
                true
        )
    }

    private fun Podcast.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_search_song,
                MediaId.podcastId(this.id),
                title,
                artist,
                true
        )
    }

    private fun SearchResult.toSearchDisplayableItem(context: Context): DisplayableItem {
        val subtitle = when (this.itemType) {
            RecentSearchesTypes.SONG -> context.getString(R.string.search_type_track)
            RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
            RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
            RecentSearchesTypes.PLAYLIST -> context.getString(R.string.search_type_playlist)
            RecentSearchesTypes.GENRE -> context.getString(R.string.search_type_genre)
            RecentSearchesTypes.FOLDER -> context.getString(R.string.search_type_folder)
            RecentSearchesTypes.PODCAST -> context.getString(R.string.search_type_podcast)
            RecentSearchesTypes.PODCAST_PLAYLIST -> context.getString(R.string.search_type_podcast_playlist)
            RecentSearchesTypes.PODCAST_ALBUM -> context.getString(R.string.search_type_podcast_album)
            RecentSearchesTypes.PODCAST_ARTIST -> context.getString(R.string.search_type_podcast_artist)
            else -> throw IllegalArgumentException("invalid item type $itemType")
        }

        val isPlayable = this.itemType == RecentSearchesTypes.SONG || this.itemType == RecentSearchesTypes.PODCAST

        val layout = when (this.itemType) {
            RecentSearchesTypes.ARTIST,
            RecentSearchesTypes.PODCAST_ARTIST -> R.layout.item_search_recent_artist
            RecentSearchesTypes.ALBUM,
            RecentSearchesTypes.PODCAST_ALBUM -> R.layout.item_search_recent_album
            else -> R.layout.item_search_recent
        }

        return DisplayableItem(
                layout,
                this.mediaId,
                this.title,
                subtitle,
                isPlayable
        )
    }

}

internal class SearchDataSourceFactory @Inject constructor(
        dataSourceProvider: Provider<SearchDataSource>
) : BaseDataSourceFactory<DisplayableItem, SearchDataSource>(dataSourceProvider) {

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