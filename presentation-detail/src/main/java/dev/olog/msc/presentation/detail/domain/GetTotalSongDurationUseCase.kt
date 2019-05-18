package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.*
import javax.inject.Inject

class GetTotalSongDurationUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {

    fun execute(mediaId: MediaId, filter: Filter): Int {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getSongListByParamDuration(mediaId.categoryValue, filter)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getSongListByParamDuration(
                mediaId.categoryValue.toLong(),
                filter
            )
            MediaIdCategory.ALBUMS -> albumGateway.getSongListByParamDuration(mediaId.categoryValue.toLong(), filter)
            MediaIdCategory.ARTISTS -> artistGateway.getSongListByParamDuration(mediaId.categoryValue.toLong(), filter)
            MediaIdCategory.GENRES -> genreGateway.getSongListByParamDuration(mediaId.categoryValue.toLong(), filter)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getPodcastListByParamDuration(
                mediaId.categoryValue.toLong(),
                filter
            )
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getPodcastListByParamDuration(
                mediaId.categoryValue.toLong(),
                filter
            )
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getPodcastListByParamDuration(
                mediaId.categoryValue.toLong(),
                filter
            )
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }
}