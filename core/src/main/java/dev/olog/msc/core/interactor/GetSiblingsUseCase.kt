package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.*
import javax.inject.Inject

class GetSiblingsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway
) {

    fun getData(mediaId: MediaId): DataRequest<*> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getSiblings(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getSiblings(mediaId)
            MediaIdCategory.ALBUMS -> albumGateway.getSiblings(mediaId)
            MediaIdCategory.ARTISTS -> artistGateway.getSiblings(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getSiblings(mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getSiblings(mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getSiblings(mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getSiblings(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

    fun canShow(mediaId: MediaId, filter: Filter): Boolean {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.PLAYLISTS -> playlistGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.ALBUMS -> albumGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.ARTISTS -> artistGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.GENRES -> genreGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.canShowSiblings(mediaId, filter)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.canShowSiblings(mediaId, filter)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}