package dev.olog.msc.presentation.popup

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.item.*
import dev.olog.msc.presentation.popup.album.AlbumPopup
import dev.olog.msc.presentation.popup.artist.ArtistPopup
import dev.olog.msc.presentation.popup.folder.FolderPopup
import dev.olog.msc.presentation.popup.genre.GenrePopup
import dev.olog.msc.presentation.popup.playlist.PlaylistPopup
import dev.olog.msc.presentation.popup.podcast.PodcastPopup
import dev.olog.msc.presentation.popup.podcastalbum.PodcastAlbumPopup
import dev.olog.msc.presentation.popup.podcastartist.PodcastArtistPopup
import dev.olog.msc.presentation.popup.podcastplaylist.PodcastPlaylistPopup
import dev.olog.msc.presentation.popup.song.SongPopup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PopupMenuFactory @Inject constructor(
    private val getFolderUseCase: GetFolderUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getGenreUseCase: GetGenreUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getPodcastPlaylistUseCase: GetPodcastPlaylistUseCase,
    private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
    private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
    private val listenerFactory: MenuListenerFactory

) {

    fun show(view: View, mediaId: MediaId) = GlobalScope.launch {
        val category = mediaId.category
        val popup: PopupMenu? = when (category) {
            MediaIdCategory.FOLDERS -> getFolderPopup(view, mediaId)
            MediaIdCategory.PLAYLISTS -> getPlaylistPopup(view, mediaId)
            MediaIdCategory.SONGS -> getSongPopup(view, mediaId)
            MediaIdCategory.ALBUMS -> getAlbumPopup(view, mediaId)
            MediaIdCategory.ARTISTS -> getArtistPopup(view, mediaId)
            MediaIdCategory.GENRES -> getGenrePopup(view, mediaId)
            MediaIdCategory.PODCASTS -> getPodcastPopup(view, mediaId)
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumPopup(view, mediaId)
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistPopup(view, mediaId)
            else -> throw IllegalArgumentException("invalid category $category")
        }
        withContext(Dispatchers.Main) {
            popup?.show()
        }
    }

    private suspend fun getFolderPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val folder = getFolderUseCase.execute(mediaId).getItem() ?: return null
        var song: Song? = null
        if (mediaId.isLeaf) {
            song = getSongUseCase.execute(mediaId).getItem()
        }
        return FolderPopup(view, folder, song, listenerFactory.folder(activity, folder, song))
    }

    private suspend fun getPlaylistPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val playlist = getPlaylistUseCase.execute(mediaId).getItem() ?: return null
        var song: Song? = null
        if (mediaId.isLeaf) {
            song = getSongUseCase.execute(mediaId).getItem()
        }
        return PlaylistPopup(view, playlist, song, listenerFactory.playlist(activity, playlist, song))
    }

    private suspend fun getSongPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val song = getSongUseCase.execute(mediaId).getItem() ?: return null
        return SongPopup(view, song, listenerFactory.song(activity, song))

    }

    private suspend fun getAlbumPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val album = getAlbumUseCase.execute(mediaId).getItem() ?: return null
        var song: Song? = null
        if (mediaId.isLeaf) {
            song = getSongUseCase.execute(mediaId).getItem()
        }
        return AlbumPopup(view, album, song, listenerFactory.album(activity, album, song))
    }

    private suspend fun getArtistPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val artist = getArtistUseCase.execute(mediaId).getItem() ?: return null
        var song: Song? = null
        if (mediaId.isLeaf) {
            song = getSongUseCase.execute(mediaId).getItem()
        }
        return ArtistPopup(view, artist, song, listenerFactory.artist(activity, artist, song))
    }

    private suspend fun getGenrePopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val genre = getGenreUseCase.execute(mediaId).getItem() ?: return null
        var song: Song? = null
        if (mediaId.isLeaf) {
            song = getSongUseCase.execute(mediaId).getItem()
        }
        return GenrePopup(view, genre, song, listenerFactory.genre(activity, genre, song))
    }

    private suspend fun getPodcastPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val podcast = getPodcastUseCase.execute(mediaId).getItem() ?: return null
        return PodcastPopup(view, podcast, listenerFactory.podcast(activity, podcast))
    }

    private suspend fun getPodcastPlaylistPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val playlist = getPodcastPlaylistUseCase.execute(mediaId).getItem() ?: return null
        var podcast: Podcast? = null
        if (mediaId.isLeaf) {
            podcast = getPodcastUseCase.execute(mediaId).getItem()
        }
        return PodcastPlaylistPopup(
            view,
            playlist,
            podcast,
            listenerFactory.podcastPlaylist(activity, playlist, podcast)
        )
    }

    private suspend fun getPodcastAlbumPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val album = getPodcastAlbumUseCase.execute(mediaId).getItem() ?: return null
        var podcast: Podcast? = null
        if (mediaId.isLeaf) {
            podcast = getPodcastUseCase.execute(mediaId).getItem()
        }
        return PodcastAlbumPopup(view, album, podcast, listenerFactory.podcastAlbum(activity, album, podcast))
    }

    private suspend fun getPodcastArtistPopup(view: View, mediaId: MediaId): PopupMenu? {
        val activity = view.context as FragmentActivity
        val artist = getPodcastArtistUseCase.execute(mediaId).getItem() ?: return null
        var podcast: Podcast? = null
        if (mediaId.isLeaf) {
            podcast = getPodcastUseCase.execute(mediaId).getItem()
        }
        return PodcastArtistPopup(view, artist, podcast, listenerFactory.podcastArtist(activity, artist, podcast))
    }

}