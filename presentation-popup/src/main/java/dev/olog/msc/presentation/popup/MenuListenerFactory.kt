package dev.olog.msc.presentation.popup

import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.presentation.popup.album.AlbumPopupListener
import dev.olog.msc.presentation.popup.artist.ArtistPopupListener
import dev.olog.msc.presentation.popup.folder.FolderPopupListener
import dev.olog.msc.presentation.popup.genre.GenrePopupListener
import dev.olog.msc.presentation.popup.playlist.PlaylistPopupListener
import dev.olog.msc.presentation.popup.podcast.PodcastPopupListener
import dev.olog.msc.presentation.popup.podcastalbum.PodcastAlbumPopupListener
import dev.olog.msc.presentation.popup.podcastartist.PodcastArtistPopupListener
import dev.olog.msc.presentation.popup.podcastplaylist.PodcastPlaylistPopupListener
import dev.olog.msc.presentation.popup.song.SongPopupListener
import javax.inject.Inject
import javax.inject.Provider

class MenuListenerFactory @Inject constructor(
        private val folderPopupListener: Provider<FolderPopupListener>,
        private val playlistPopupListener: Provider<PlaylistPopupListener>,
        private val songPopupListener: Provider<SongPopupListener>,
        private val albumPopupListener: Provider<AlbumPopupListener>,
        private val artistPopupListener: Provider<ArtistPopupListener>,
        private val genrePopupListener: Provider<GenrePopupListener>,
        private val podcastPopupListener: Provider<PodcastPopupListener>,
        private val podcastPlaylistPopupListener: Provider<PodcastPlaylistPopupListener>,
        private val podcastAlbumPopupListener: Provider<PodcastAlbumPopupListener>,
        private val podcastArtistPopupListener: Provider<PodcastArtistPopupListener>
) {

    fun folder(activity: FragmentActivity, folder: Folder, song: Song?) = folderPopupListener.get().setData(folder, song).setActivity(activity)
    fun playlist(activity: FragmentActivity, playlist: Playlist, song: Song?) = playlistPopupListener.get().setData(playlist, song).setActivity(activity)
    fun song(activity: FragmentActivity, song: Song) = songPopupListener.get().setData(song).setActivity(activity)
    fun album(activity: FragmentActivity, album: Album, song: Song?) = albumPopupListener.get().setData(album, song).setActivity(activity)
    fun artist(activity: FragmentActivity, artist: Artist, song: Song?) = artistPopupListener.get().setData(artist, song).setActivity(activity)
    fun genre(activity: FragmentActivity, genre: Genre, song: Song?) = genrePopupListener.get().setData(genre, song).setActivity(activity)
    fun podcast(activity: FragmentActivity, podcast: Podcast) = podcastPopupListener.get().setData(podcast).setActivity(activity)
    fun podcastPlaylist(activity: FragmentActivity, podcastPlaylist: PodcastPlaylist, song: Podcast?) = podcastPlaylistPopupListener.get().setData(podcastPlaylist, song).setActivity(activity)
    fun podcastAlbum(activity: FragmentActivity, podcastAlbum: PodcastAlbum, song: Podcast?) = podcastAlbumPopupListener.get().setData(podcastAlbum, song).setActivity(activity)
    fun podcastArtist(activity: FragmentActivity, podcastArtist: PodcastArtist, song: Podcast?) = podcastArtistPopupListener.get().setData(podcastArtist, song).setActivity(activity)

}