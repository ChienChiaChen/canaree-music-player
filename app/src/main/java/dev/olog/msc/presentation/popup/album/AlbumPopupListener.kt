package dev.olog.msc.presentation.popup.album

import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.presentation.base.interfaces.MediaProvider
import javax.inject.Inject

class AlbumPopupListener @Inject constructor(
        private val navigator: Navigator,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase,
        private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var album: Album
    private var song: Song? = null

    fun setData(album: Album, song: Song?): AlbumPopupListener{
        this.album = album
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            return MediaId.playableItem(MediaId.albumId(album.id), song!!.id)
        } else {
            return MediaId.albumId(album.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), album.songs, album.title)

        when (itemId){
            AbsPopup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.playLater -> playLater()
            R.id.playNext -> playNext()
            R.id.delete -> delete()
            R.id.viewArtist -> viewArtist()
            R.id.viewAlbum -> viewAlbum(navigator, MediaId.albumId(song!!.albumId))
            R.id.viewInfo -> viewInfo(navigator, getMediaId())
            R.id.share -> share(activity, song!!)
            R.id.setRingtone -> setRingtone(navigator, getMediaId(), song!!)
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), album.title, album.image)
        }

        return true
    }

    private fun toCreatePlaylist(){
        if (song == null){
            navigator.toCreatePlaylistDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toCreatePlaylistDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playFromMediaId(){
        (activity as MediaProvider).playFromMediaId(getMediaId())
    }

    private fun playShuffle(){
        (activity as MediaProvider).shuffle(getMediaId())
    }

    private fun playLater(){
        if (song == null){
            navigator.toPlayLater(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext(){
        if (song == null){
            navigator.toPlayNext(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }


    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(activity, getMediaId(), album.songs, album.title)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun viewArtist(){
        navigator.toDetailFragment(activity, MediaId.artistId(album.artistId))
    }


}