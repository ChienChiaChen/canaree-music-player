package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.*
import javax.inject.Inject

class GetSongListChunkByParamUseCase @Inject constructor(
    private val genreGateway: GenreGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val folderGateway: FolderGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {

    fun execute(mediaId: MediaId): PageRequest<*> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getSongListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ALBUMS -> albumGateway.getSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.getSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreGateway.getSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getPodcastListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getPodcastListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getPodcastListByParam(mediaId.categoryValue.toLong())
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}
