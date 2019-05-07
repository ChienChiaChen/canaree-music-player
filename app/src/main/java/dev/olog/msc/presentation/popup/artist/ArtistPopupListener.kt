package dev.olog.msc.presentation.popup.artist

import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.domain.interactor.all.GetPlaylistsBlockingUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.presentation.base.interfaces.MediaProvider
import javax.inject.Inject

class ArtistPopupListener @Inject constructor(
        private val navigator: Navigator,
        getPlaylistBlockingUseCase: GetPlaylistsBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase,
        private val appShortcuts: AppShortcuts

) : AbsPopupListener(getPlaylistBlockingUseCase, addToPlaylistUseCase, false) {

    private lateinit var artist: Artist
    private var song: Song? = null

    fun setData(artist: Artist, song: Song?): ArtistPopupListener{
        this.artist = artist
        this.song = song
        return this
    }

    private fun getMediaId(): MediaId {
        if (song != null){
            return MediaId.playableItem(MediaId.artistId(artist.id), song!!.id)
        } else {
            return MediaId.artistId(artist.id)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        onPlaylistSubItemClick(activity, itemId, getMediaId(), artist.songs, artist.name)

        when (itemId){
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
            R.id.addHomeScreen -> appShortcuts.addDetailShortcut(getMediaId(), artist.name, artist.image)
        }


        return true
    }

    private fun toCreatePlaylist(){
        if (song == null){
            navigator.toCreatePlaylistDialog(activity, getMediaId(), artist.songs, artist.name)
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
            navigator.toPlayLater(activity, getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayLater(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun playNext(){
        if (song == null){
            navigator.toPlayNext(activity, getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toPlayNext(activity, getMediaId(), -1, song!!.title)
        }
    }



    private fun addToFavorite(){
        if (song == null){
            navigator.toAddToFavoriteDialog(activity, getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toAddToFavoriteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

    private fun delete(){
        if (song == null){
            navigator.toDeleteDialog(activity, getMediaId(), artist.songs, artist.name)
        } else {
            navigator.toDeleteDialog(activity, getMediaId(), -1, song!!.title)
        }
    }

}