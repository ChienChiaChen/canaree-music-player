package dev.olog.msc.presentation.popup.podcastalbum

import android.view.MenuItem
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import javax.inject.Inject

class PodcastAlbumPopupListener @Inject constructor(
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase,
    private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, true) {

    private lateinit var album: PodcastAlbum
    private var podcast: Podcast? = null

    fun setData(album: PodcastAlbum, podcast: Podcast?): PodcastAlbumPopupListener {
        this.album = album
        this.podcast = podcast
        return this
    }

    private fun getMediaId(): MediaId {
        if (podcast != null) {
            return MediaId.playableItem(MediaId.podcastAlbumId(album.id), podcast!!.id)
        } else {
            return MediaId.podcastAlbumId(album.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.songs, album.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.podcastAlbumId(podcast!!.albumId))
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.share -> share(activity, podcast!!.toSong())
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), album.title)
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (podcast == null) {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playFromMediaId() {
        (activity as MediaProvider).playFromMediaId(getMediaId())
    }

    private fun playShuffle() {
        (activity as MediaProvider).shuffle(getMediaId())
    }

    private fun playLater() {
        if (podcast == null) {
            navigator.toPlayLater(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun playNext() {
        if (podcast == null) {
            navigator.toPlayNext(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, podcast!!.title)
        }
    }


    private fun addToFavorite() {
        if (podcast == null) {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun delete() {
        if (podcast == null) {
            navigator.toDeleteDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, podcast!!.title)
        }
    }

    private fun viewArtist() {
        navigator.toDetailFragment(activity, MediaId.podcastArtistId(album.artistId))
    }


}