package dev.olog.msc.presentation.popup.genre

import android.content.Context
import android.view.MenuItem
import dev.olog.msc.appshortcuts.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetPlaylistsBlockingUseCase
import dev.olog.msc.presentation.media.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.presentation.popup.domain.AddToPlaylistUseCase
import javax.inject.Inject

class GenrePopupListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigator: Navigator,
    getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
    addToPlaylistUseCase: AddToPlaylistUseCase

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var genre: Genre
    private var song: Song? = null

    fun setData(genre: Genre, song: Song?): GenrePopupListener {
        this.genre = genre
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null) {
            return MediaId.playableItem(MediaId.genreId(genre.id), song!!.id)
        } else {
            return MediaId.genreId(genre.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), genre.size, genre.name)

        when (itemId) {
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewArtist -> viewArtist(navigator, MediaId.artistId(song!!.artistId))
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> AppShortcuts.instance(context).addDetailShortcut(getMediaId(), genre.name)
        }

        return true
    }

    private fun toCreatePlaylist() {
        if (song == null) {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId() {
        (activity as MediaProvider).playFromMediaId(getMediaId())
    }

    private fun playShuffle() {
        (activity as MediaProvider).shuffle(getMediaId())
    }

    private fun playLater() {
        if (song == null) {
            navigator.toPlayLater(activity, getMediaId(), genre.size, genre.name)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext() {
        if (song == null) {
            navigator.toPlayNext(activity, getMediaId(), genre.size, genre.name)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite() {
        navigator.toAddToFavoriteDialog(activity, getMediaId(), song!!.title)

    }

    private fun delete() {
        if (song == null) {
            navigator.toDeleteDialog(activity, getMediaId(), genre.size, genre.name)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

}