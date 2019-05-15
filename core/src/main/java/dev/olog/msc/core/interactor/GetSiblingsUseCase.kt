package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkedData
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

    fun getChunk(mediaId: MediaId): ChunkedData<*>{
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.ALBUMS -> albumGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.ARTISTS -> artistGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.GENRES -> genreGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getSiblingsChunk(mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getSiblingsChunk(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

    fun canShow(mediaId: MediaId): Boolean {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.canShowSiblings(mediaId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.canShowSiblings(mediaId)
            MediaIdCategory.ALBUMS -> albumGateway.canShowSiblings(mediaId)
            MediaIdCategory.ARTISTS -> artistGateway.canShowSiblings(mediaId)
            MediaIdCategory.GENRES -> genreGateway.canShowSiblings(mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.canShowSiblings(mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.canShowSiblings(mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.canShowSiblings(mediaId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}