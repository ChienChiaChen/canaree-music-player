package dev.olog.msc.core.interactor

import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistgateway: PodcastPlaylistGateway

) {

    fun execute(type: PlaylistType): List<Playlist> {
        if (type == PlaylistType.PODCAST) {
            return podcastPlaylistgateway.getPlaylistsBlocking()
                .map { Playlist(it.id, it.title, it.size) }
        }
        return playlistGateway.getAll().getAll(Filter.NO_FILTER)
    }
}
