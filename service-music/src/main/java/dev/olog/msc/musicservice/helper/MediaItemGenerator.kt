package dev.olog.msc.musicservice.helper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

// TODO add support to playlist
internal class MediaItemGenerator @Inject constructor(
    private val getAllFoldersUseCase: FolderGateway,
    private val getAllPlaylistsUseCase: PlaylistGateway,
    private val getAllSongsUseCase: SongGateway,
    private val getAllAlbumsUseCase: AlbumGateway,
    private val getAllArtistsUseCase: ArtistGateway,
    private val getAllGenresUseCase: GenreGateway,
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase
) {


    fun getCategoryChilds(category: MediaIdCategory): MutableList<MediaBrowserCompat.MediaItem> {
        assertBackgroundThread()
        val request = Request(Page.NO_PAGING, Filter.NO_FILTER)
        return when (category) {
            MediaIdCategory.FOLDERS -> getAllFoldersUseCase.getAll().getPage(request).map { it.toMediaItem() }
            MediaIdCategory.PLAYLISTS -> getAllPlaylistsUseCase.getAll().getPage(request).map { it.toMediaItem() }
            MediaIdCategory.SONGS -> getAllSongsUseCase.getAll().getPage(request).map { it.toMediaItem() }
            MediaIdCategory.ALBUMS -> getAllAlbumsUseCase.getAll().getPage(request).map { it.toMediaItem() }
            MediaIdCategory.ARTISTS -> getAllArtistsUseCase.getAll().getPage(request).map { it.toMediaItem() }
            MediaIdCategory.GENRES -> getAllGenresUseCase.getAll().getPage(request).map { it.toMediaItem() }
            else -> throw IllegalArgumentException("invalid category $category")
        }.toMutableList()
    }

    fun getCategoryValueChilds(parentId: MediaId): MutableList<MediaBrowserCompat.MediaItem> {
        assertBackgroundThread()
        val request = Request(Page.NO_PAGING, Filter.NO_FILTER)
        return getSongListByParamUseCase.execute(parentId).getPage(request).filter { it is Song }
            .map { it as Song }
            .map { it.toChildMediaItem(parentId) }
            .toMutableList()
    }

    private fun Folder.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.folderId(this.path).toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Playlist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.playlistId(this.id).toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Song.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.songId(this.id).toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Song.toChildMediaItem(parentId: MediaId): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.playableItem(parentId, this.id).toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun Album.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.albumId(this.id).toString())
            .setTitle(this.title)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Artist.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.artistId(this.id).toString())
            .setTitle(this.name)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

    private fun Genre.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(MediaId.genreId(this.id).toString())
            .setTitle(this.name)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
    }

}