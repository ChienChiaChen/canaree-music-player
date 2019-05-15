package dev.olog.msc.presentation.popup.playlist

import android.view.View

import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.shared.TrackUtils

class PlaylistPopup(
        view: View,
        playlist: Playlist,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_playlist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null){
            if (PlaylistGateway.isAutoPlaylist(playlist.id)){
                menu.removeItem(R.id.rename)
                menu.removeItem(R.id.delete)
                menu.removeItem(R.id.removeDuplicates)
            }
            if (playlist.id == PlaylistGateway.LAST_ADDED_ID){
                menu.removeItem(R.id.clear)
            }
            if (playlist.size < 1){
                menu.removeItem(R.id.play)
                menu.removeItem(R.id.playShuffle)
                menu.removeItem(R.id.addToFavorite)
                menu.removeItem(R.id.playLater)
                menu.removeItem(R.id.playNext)
                menu.removeItem(R.id.addToPlaylist)
                menu.removeItem(R.id.clear)
            }
        } else {
            if (song.artist == TrackUtils.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
            if (song.album == TrackUtils.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
            if (playlist.id == PlaylistGateway.FAVORITE_LIST_ID){
                menu.removeItem(R.id.addToFavorite)
            }
        }
    }

}