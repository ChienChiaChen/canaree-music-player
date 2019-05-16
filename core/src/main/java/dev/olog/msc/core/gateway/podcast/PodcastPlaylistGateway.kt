package dev.olog.msc.core.gateway.podcast

import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.gateway.base.BaseGateway
import dev.olog.msc.core.gateway.base.ChildsHasPodcasts
import dev.olog.msc.core.gateway.base.HasSiblings

interface PodcastPlaylistGateway :
    BaseGateway<PodcastPlaylist, Long>,
    ChildsHasPodcasts<Long>,
    HasSiblings<PodcastPlaylist> {

    companion object {

        const val PODCAST_LAST_ADDED_ID: Long = -6000
        const val PODCAST_FAVORITE_LIST_ID: Long = -60012
        const val PODCAST_HISTORY_LIST_ID: Long = -60018

        private val podcastAutoPlaylists = listOf(
            PODCAST_LAST_ADDED_ID,
            PODCAST_FAVORITE_LIST_ID,
            PODCAST_HISTORY_LIST_ID
        )

        fun isPodcastAutoPlaylist(id: Long) = podcastAutoPlaylists.contains(id)
    }

    fun getAllAutoPlaylists(): List<PodcastPlaylist>
    fun getPlaylistsBlocking(): List<PodcastPlaylist>

    suspend fun createPlaylist(playlistName: String): Long

    suspend fun renamePlaylist(playlistId: Long, newTitle: String)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun clearPlaylist(playlistId: Long)

    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)

    suspend fun removeSongFromPlaylist(playlistId: Long, idInPlaylist: Long)

    suspend fun removeDuplicated(playlistId: Long)

    suspend fun insertPodcastToHistory(podcastId: Long)

}