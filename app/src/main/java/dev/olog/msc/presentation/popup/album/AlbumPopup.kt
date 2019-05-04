package dev.olog.msc.presentation.popup.album

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.core.entity.track.Album
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.shared.TrackUtils

class AlbumPopup(
        view: View,
        album: Album,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view)  {

    init {
        if (song == null){
            inflate(R.menu.dialog_album)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song == null){
            if (album.artist == TrackUtils.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
        } else {
            menu.removeItem(R.id.viewAlbum)

            if (song.artist == TrackUtils.UNKNOWN){
                menu.removeItem(R.id.viewArtist)
            }
        }
    }

}