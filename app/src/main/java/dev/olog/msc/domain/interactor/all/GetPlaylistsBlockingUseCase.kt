package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.PlaylistGateway
import dev.olog.msc.core.gateway.PodcastPlaylistGateway
import javax.inject.Inject

class GetPlaylistsBlockingUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistgateway: PodcastPlaylistGateway

)  {

    fun execute(type: PlaylistType): List<Playlist>{
        if (type == PlaylistType.PODCAST){
            return podcastPlaylistgateway.getPlaylistsBlocking()
                    .map { Playlist(it.id, it.title, it.size) }
        }
        return playlistGateway.getPlaylistsBlocking()
    }
}
