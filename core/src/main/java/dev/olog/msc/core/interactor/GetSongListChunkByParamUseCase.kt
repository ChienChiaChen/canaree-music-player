package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.ChunkedData
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

    fun execute(mediaId: MediaId): ChunkedData<*> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getSongListByParamChunk(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getSongListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.ALBUMS -> albumGateway.getSongListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.getSongListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreGateway.getSongListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getPodcastListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getPodcastListByParamChunk(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getPodcastListByParamChunk(mediaId.categoryValue.toLong())
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}
