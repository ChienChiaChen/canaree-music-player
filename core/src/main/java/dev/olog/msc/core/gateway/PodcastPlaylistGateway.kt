package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PodcastPlaylistGateway :
        BaseGateway<PodcastPlaylist, Long>,
        ChildsHasPodcasts<Long>,
        HasMostPlayed {

    companion object {

        const val PODCAST_LAST_ADDED_ID: Long = -6000
        const val PODCAST_FAVORITE_LIST_ID: Long = -60012
        const val PODCAST_HISTORY_LIST_ID: Long = -60018

        private val podcastAutoPlaylists = listOf(
                PODCAST_LAST_ADDED_ID, PODCAST_FAVORITE_LIST_ID, PODCAST_HISTORY_LIST_ID
        )

        fun isPodcastAutoPlaylist(id: Long) = podcastAutoPlaylists.contains(id)
    }

    fun getAllAutoPlaylists() : Observable<List<PodcastPlaylist>>

    fun createPlaylist(playlistName: String): Single<Long>

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable

    fun deletePlaylist(playlistId: Long): Completable

    fun clearPlaylist(playlistId: Long): Completable

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable

    fun getPlaylistsBlocking(): List<PodcastPlaylist>

    fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long): Completable

    fun removeDuplicated(playlistId: Long): Completable

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean

    fun insertPodcastToHistory(podcastId: Long): Completable

}