package dev.olog.msc.presentation.tabs.data

import android.content.res.Resources
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.interactor.added.GetRecentlyAddedAlbumsUseCase
import dev.olog.msc.core.interactor.added.GetRecentlyAddedArtistsUseCase
import dev.olog.msc.core.interactor.added.GetRecentlyAddedPodcastsAlbumsUseCase
import dev.olog.msc.core.interactor.added.GetRecentlyAddedPodcastsArtistsUseCase
import dev.olog.msc.core.interactor.all.*
import dev.olog.msc.core.interactor.played.GetLastPlayedAlbumsUseCase
import dev.olog.msc.core.interactor.played.GetLastPlayedArtistsUseCase
import dev.olog.msc.core.interactor.played.GetLastPlayedPodcastAlbumsUseCase
import dev.olog.msc.core.interactor.played.GetLastPlayedPodcastArtistsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.domain.GetAllAlbumsSortedUseCase
import dev.olog.msc.presentation.tabs.domain.GetAllArtistsSortedUseCase
import dev.olog.msc.presentation.tabs.domain.GetAllSongsSortedUseCase
import dev.olog.msc.shared.extensions.doIf
import dev.olog.msc.shared.extensions.mapToList
import dev.olog.msc.shared.extensions.startWith
import dev.olog.msc.shared.extensions.startWithIfNotEmpty
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class TabFragmentDataProvider @Inject constructor(
    private val resources: Resources,
    private val headers: TabFragmentHeaders,
    private val folderUseCase: GetAllFoldersUseCase,
    private val playlistUseCase: GetAllPlaylistsUseCase,
    private val autoPlaylistUseCase: GetAllAutoPlaylistUseCase,
    private val songsUseCase: GetAllSongsSortedUseCase,
    private val albumsUseCase: GetAllAlbumsSortedUseCase,
    private val lastPlayedAlbumsUseCase: GetLastPlayedAlbumsUseCase,
    private val newAlbumsUseCase: GetRecentlyAddedAlbumsUseCase,
    private val artistUseCase: GetAllArtistsSortedUseCase,
    private val lastPlayedArtistsUseCase: GetLastPlayedArtistsUseCase,
    private val newArtistsUseCase: GetRecentlyAddedArtistsUseCase,
    private val genresUseCase: GetAllGenresUseCase,

    private val podcastPlaylistUseCase: GetAllPodcastPlaylistUseCase,
    private val autoPodcastPlaylistUseCase: GetAllPodcastsAutoPlaylistUseCase,
    private val podcastUseCase: GetAllPodcastUseCase,
    private val podcastArtistsUseCase: GetAllPodcastArtistsUseCase,
    private val lastPlayedPodcastArtistsUseCase: GetLastPlayedPodcastArtistsUseCase,
    private val newPodcastArtistsUseCase: GetRecentlyAddedPodcastsArtistsUseCase,
    private val podcastAlbumsUseCase: GetAllPodcastAlbumsUseCase,
    private val lastPlayedPodcastAlbumsUseCase: GetLastPlayedPodcastAlbumsUseCase,
    private val newPodcastAlbumsUseCase: GetRecentlyAddedPodcastsAlbumsUseCase
) {


    fun getData(category: MediaIdCategory): Observable<List<DisplayableItem>> {
        return when (category) {
            MediaIdCategory.FOLDERS -> folderUseCase.execute().mapToList { it.toTabDisplayableItem(resources) }
            MediaIdCategory.PLAYLISTS -> providePlaylist()
            MediaIdCategory.SONGS -> songsUseCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.startWithIfNotEmpty(headers.shuffleHeader) }
            MediaIdCategory.ALBUMS -> provideAlbums()
            MediaIdCategory.ARTISTS -> provideArtists()
            MediaIdCategory.GENRES -> genresUseCase.execute().mapToList { it.toTabDisplayableItem(resources) }
            MediaIdCategory.RECENT_ALBUMS -> lastPlayedAlbumsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
            MediaIdCategory.RECENT_ARTISTS -> lastPlayedArtistsUseCase.execute().mapToList {
                it.toTabLastPlayedDisplayableItem(
                    resources
                )
            }
            MediaIdCategory.NEW_ALBUMS -> newAlbumsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
            MediaIdCategory.NEW_ARTISTS -> newArtistsUseCase.execute().mapToList {
                it.toTabLastPlayedDisplayableItem(
                    resources
                )
            }

            MediaIdCategory.PODCASTS_PLAYLIST -> providePodcastPlaylist()
            MediaIdCategory.PODCASTS -> podcastUseCase.execute().mapToList { it.toTabDisplayableItem(resources) }
            MediaIdCategory.PODCASTS_ARTISTS -> providePodcastArtists()
            MediaIdCategory.PODCASTS_ALBUMS -> providePodcastAlbums()
            MediaIdCategory.RECENT_PODCAST_ALBUMS -> lastPlayedPodcastAlbumsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
            MediaIdCategory.RECENT_PODCAST_ARTISTS -> lastPlayedPodcastArtistsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem(resources) }
            MediaIdCategory.NEW_PODCSAT_ALBUMS -> newPodcastAlbumsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
            MediaIdCategory.NEW_PODCSAT_ARTISTS -> newPodcastArtistsUseCase.execute().mapToList { it.toTabLastPlayedDisplayableItem(resources) }
            else -> throw IllegalStateException("invalid category $category")
        }
    }

    private fun providePlaylist(): Observable<List<DisplayableItem>> {
        val playlistObs = playlistUseCase.execute()
            .mapToList { it.toTabDisplayableItem(resources) }
            .map { it.startWithIfNotEmpty(headers.allPlaylistHeader) }


        val autoPlaylistObs = autoPlaylistUseCase.execute()
            .mapToList { it.toAutoPlaylist() }
            .map { it.startWith(headers.autoPlaylistHeader) }


        return Observables.combineLatest(playlistObs, autoPlaylistObs) { playlist, autoPlaylist ->
            autoPlaylist.plus(playlist)
        }
    }

    private fun provideAlbums(): Observable<List<DisplayableItem>> {
        val allObs = albumsUseCase.execute()
            .mapToList { it.toTabDisplayableItem() }
            .map { it.toMutableList() }


        val lastPlayedObs = Observables.combineLatest(
            lastPlayedAlbumsUseCase.execute().distinctUntilChanged(),
            newAlbumsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newAlbumsHeaders) }
                .doIf(last.count() > 0) { addAll(headers.recentAlbumHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
        }.distinctUntilChanged()


        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
    }

    private fun provideArtists(): Observable<List<DisplayableItem>> {
        val allObs = artistUseCase.execute()
            .mapToList { it.toTabDisplayableItem(resources) }
            .map { it.toMutableList() }


        val lastPlayedObs = Observables.combineLatest(
            lastPlayedArtistsUseCase.execute().distinctUntilChanged(),
            newArtistsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newArtistsHeaders) }
                .doIf(last.count() > 0) { addAll(headers.recentArtistHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
        }.distinctUntilChanged()


        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
    }

    private fun providePodcastPlaylist(): Observable<List<DisplayableItem>> {
        val autoPlaylistObs = autoPodcastPlaylistUseCase.execute()
            .mapToList { it.toAutoPlaylist() }
            .map { it.startWith(headers.autoPlaylistHeader) }

        val playlistObs = podcastPlaylistUseCase.execute()
            .mapToList { it.toTabDisplayableItem(resources) }
            .map { it.startWithIfNotEmpty(headers.allPlaylistHeader) }

        return Observables.combineLatest(playlistObs, autoPlaylistObs) { playlist, autoPlaylist ->
            autoPlaylist.plus(playlist)
        }
    }

    private fun providePodcastArtists(): Observable<List<DisplayableItem>> {
        val allObs = podcastArtistsUseCase.execute()
            .mapToList { it.toTabDisplayableItem(resources) }
            .map { it.toMutableList() }


        val lastPlayedObs = Observables.combineLatest(
            lastPlayedPodcastArtistsUseCase.execute().distinctUntilChanged(),
            newPodcastArtistsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newArtistsHeaders) }
                .doIf(last.count() > 0) { addAll(headers.recentArtistHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
        }.distinctUntilChanged()


        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
    }

    private fun providePodcastAlbums(): Observable<List<DisplayableItem>> {
        val allObs = podcastAlbumsUseCase.execute()
            .mapToList { it.toTabDisplayableItem() }
            .map { it.toMutableList() }


        val lastPlayedObs = Observables.combineLatest(
            lastPlayedPodcastAlbumsUseCase.execute().distinctUntilChanged(),
            newPodcastAlbumsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newAlbumsHeaders) }
                .doIf(last.count() > 0) { addAll(headers.recentAlbumHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
        }.distinctUntilChanged()


        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
    }

}