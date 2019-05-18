package dev.olog.msc.presentation.search.paging

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.data.request.with
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.RecentSearchesGateway
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.search.R
import dev.olog.msc.presentation.search.SearchFragmentHeaders
import dev.olog.msc.shared.RecentSearchesTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

// TODO show songs and podcast ??
internal class SearchDataSource @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val displayableHeaders: SearchFragmentHeaders,
    private val recentSearchesGateway: RecentSearchesGateway
) : BaseDataSource<DisplayableItem>() {

    var filterBy: String = ""

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@SearchDataSource) }
            songGateway.getAll().observeNotification()
                .collect { invalidate() }
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
        return songGateway.getAll().getCount(filterRequest)
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        if (mainListSize == 0) {
            val headers = recentSearchesGateway.getAll().map { it.toSearchDisplayableItem(context) }.toMutableList()
            if (headers.isNotEmpty()){
                headers.add(DisplayableItem(R.layout.item_search_clear_recent, MediaId.headerId("clear recent"), ""))
                headers.addAll(0, displayableHeaders.recents)
            }
            return headers
        } else {
            val headers = mutableListOf<DisplayableItem>()

            val albumsSize = albumGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ALBUM, Filter.By.ARTIST)))
            val artistsSize = artistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.ARTIST)))
            val playlistsSize = playlistGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))
            val foldersSize = folderGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))
            val genresSize = genreGateway.getAll().getCount(filterRequest.with(byColumn = arrayOf(Filter.By.TITLE)))
            val songsSize = songGateway.getAll().getCount(filterRequest)

            if (albumsSize > 0) {
                headers.addAll(displayableHeaders.albumsHeaders(albumsSize))
            }
            if (artistsSize > 0) {
                headers.addAll(displayableHeaders.artistsHeaders(albumsSize))
            }
            if (playlistsSize > 0) {
                headers.addAll(displayableHeaders.playlistsHeaders(albumsSize))
            }
            if (foldersSize > 0) {
                headers.addAll(displayableHeaders.foldersHeaders(albumsSize))
            }
            if (genresSize > 0) {
                headers.addAll(displayableHeaders.genreHeaders(genresSize))
            }
            if (songsSize > 0) {
                headers.add(displayableHeaders.songsHeaders(genresSize))
            }
            return headers
        }
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(page: Request): List<DisplayableItem> {
        return songGateway.getAll().getPage(page.with(filter = filterRequest))
            .map { it.toSearchDisplayableItem() }
    }

    private fun Song.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_song,
            MediaId.songId(this.id),
            title,
            artist,
            image,
            true
        )
    }

    private fun Podcast.toSearchDisplayableItem(): DisplayableItem { // TODO
        return DisplayableItem(
            R.layout.item_search_song,
            MediaId.podcastId(this.id),
            title,
            artist,
            image,
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
            this.image,
            isPlayable
        )
    }

}

internal class SearchDataSourceFactory @Inject constructor(
    private val dataSourceProvider: Provider<SearchDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    private var filterBy: String = ""
    private var dataSource: SearchDataSource? = null

    fun updateFilterBy(filterBy: String) {
        if (this.filterBy != filterBy) {
            this.filterBy = filterBy
            dataSource?.invalidate()
        }
    }

    override fun create(): DataSource<Int, DisplayableItem> {
        val dataSource = dataSourceProvider.get()
        this.dataSource = dataSource
        dataSource.filterBy = filterBy
        return dataSource
    }

    fun invalidate() {
        dataSource?.invalidate()
    }
}