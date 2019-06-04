package dev.olog.msc.presentation.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedPodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedPodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastPlaylistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedPodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedPodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.track.*
import kotlinx.coroutines.cancel
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
    private val sortPrefs: SortPreferencesGateway,
    // tracks
    private val folderDataSource: FolderDataSourceFactory,
    private val playlistDataSource: PlaylistDataSourceFactory,
    private val songDataSource: SongDataSourceFactory,
    private val albumDataSource: AlbumDataSourceFactory,
    private val artistDataSource: ArtistDataSourceFactory,
    private val genreDataSource: GenreDataSourceFactory,
    private val lastPlayedArtistDataSource: LastPlayedArtistDataSourceFactory,
    private val lastPlayedAlbumDataSource: LastPlayedAlbumDataSourceFactory,
    private val recentlyAddedArtistDataSource: RecentlyAddedArtistDataSourceFactory,
    private val recentlyAddedAlbumDataSource: RecentlyAddedAlbumDataSourceFactory,
    // podcast
    private val podcastDataSource: PodcastDataSourceFactory,
    private val podcastPlaylistDataSource: PodcastPlaylistDataSourceFactory,
    private val podcastAlbumDataSource: PodcastAlbumDataSourceFactory,
    private val podcastArtistDataSource: PodcastArtistDataSourceFactory,
    private val lastPlayedPodcastArtistDataSource: LastPlayedPodcastArtistDataSourceFactory,
    private val lastPlayedPodcastAlbumDataSource: LastPlayedPodcastAlbumDataSourceFactory,
    private val recentlyAddedPodcastArtistDataSource: RecentlyAddedPodcastArtistDataSourceFactory,
    private val recentlyAddedPodcastAlbumDataSource: RecentlyAddedPodcastAlbumDataSourceFactory

) : ViewModel() {

    private val liveDataList: MutableMap<TabCategory, LiveData<PagedList<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: TabCategory): LiveData<PagedList<DisplayableItem>> {
        return liveDataList.getOrPut(category) {
            val isLongList = category == TabCategory.SONGS || category == TabCategory.PODCASTS
            val config = PagedList.Config.Builder()
                .setPageSize(if (isLongList) 100 else 25)
                .setEnablePlaceholders(true)
                .build()
            LivePagedListBuilder(getFactory(category), config).build()
        }
    }

    private fun getFactory(category: TabCategory): DataSource.Factory<Int, DisplayableItem> {
        return when (category) {
            // tracks
            TabCategory.FOLDERS -> folderDataSource
            TabCategory.PLAYLISTS -> playlistDataSource
            TabCategory.SONGS -> songDataSource
            TabCategory.ALBUMS -> albumDataSource
            TabCategory.ARTISTS -> artistDataSource
            TabCategory.GENRES -> genreDataSource
            TabCategory.LAST_PLAYED_ARTISTS -> lastPlayedArtistDataSource
            TabCategory.LAST_PLAYED_ALBUMS -> lastPlayedAlbumDataSource
            TabCategory.RECENTLY_ADDED_ARTISTS -> recentlyAddedArtistDataSource
            TabCategory.RECENTLY_ADDED_ALBUMS -> recentlyAddedAlbumDataSource
            // podcasts
            TabCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataSource
            TabCategory.PODCASTS -> podcastDataSource
            TabCategory.PODCASTS_ALBUMS -> podcastAlbumDataSource
            TabCategory.PODCASTS_ARTISTS -> podcastArtistDataSource
            TabCategory.LAST_PLAYED_PODCAST_ALBUMS -> lastPlayedPodcastAlbumDataSource
            TabCategory.LAST_PLAYED_PODCAST_ARTISTS -> lastPlayedPodcastArtistDataSource
            TabCategory.RECENTLY_ADDED_PODCAST_ALBUMS -> recentlyAddedPodcastAlbumDataSource
            TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> recentlyAddedPodcastArtistDataSource
            else -> throw IllegalArgumentException("invalid media category $category")
        }
    }

    fun getAllTracksSortOrder(): LibrarySortType {
        return sortPrefs.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): LibrarySortType {
        return sortPrefs.getAllAlbumsSortOrder()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        // track
        folderDataSource.onDetach()
        playlistDataSource.onDetach()
        songDataSource.onDetach()
        albumDataSource.onDetach()
        artistDataSource.onDetach()
        genreDataSource.onDetach()
        lastPlayedArtistDataSource.onDetach()
        lastPlayedAlbumDataSource.onDetach()
        recentlyAddedArtistDataSource.onDetach()
        recentlyAddedAlbumDataSource.onDetach()
        // podcast
        podcastPlaylistDataSource.onDetach()
        podcastDataSource.onDetach()
        podcastAlbumDataSource.onDetach()
        podcastArtistDataSource.onDetach()
        lastPlayedPodcastArtistDataSource.onDetach()
        lastPlayedPodcastAlbumDataSource.onDetach()
        recentlyAddedPodcastArtistDataSource.onDetach()
        recentlyAddedPodcastAlbumDataSource.onDetach()
    }

}