package dev.olog.msc.presentation.popup.playlist

import android.content.Context
import android.view.MenuItem
import dev.olog.msc.appshortcuts.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.presentation.media.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import dev.olog.msc.shared.extensions.toast
import javax.inject.Inject

class PlaylistPopupListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var playlist: Playlist
    private var song: Song? = null

    fun setData(playlist: Playlist, song: Song?): PlaylistPopupListener {
        this.playlist = playlist
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null) {
            return MediaId.playableItem(MediaId.playlistId(playlist.id), song!!.id)
        } else {
            return MediaId.playlistId(playlist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), playlist.size, playlist.title)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.rename -> rename()
            R.id.clear -> clearPlaylist()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.artistId(song!!.artistId))
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> AppShortcuts.instance(context).addDetailShortcut(getMediaId(), playlist.title)
            R.id.removeDuplicates -> removeDuplicates()
        }


        return true
    }

    private fun removeDuplicates() {
        navigator.toRemoveDuplicatesDialog(activity, MediaId.playlistId(playlist.id), playlist.title)
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            (activity as MediaProvider).playFromMediaId(getMediaId())
        }
    }

    private fun playShuffle() {
        if (playlist.size == 0) {
            activity.toast(R.string.common_empty_list)
        } else {
            (activity as MediaProvider).shuffle(getMediaId())
        }
    }

    private fun playLater() {
        if (song == null) {
            navigator.toPlayLater(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(activity, getMediaId(), song!!.title)

    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(activity, getMediaId(), playlist.size, playlist.title)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun rename() {
        navigator.toRenameDialog(activity, getMediaId(), playlist.title)
    }

    private fun clearPlaylist() {
        navigator.toClearPlaylistDialog(activity, getMediaId(), playlist.title)
    }


}