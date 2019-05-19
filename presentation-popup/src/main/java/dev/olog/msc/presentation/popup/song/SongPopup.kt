package dev.olog.msc.presentation.popup.song

import android.view.View

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.shared.TrackUtils

class SongPopup(
        view: View,
        song: Song,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_song)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song.artist == TrackUtils.UNKNOWN_ARTIST){
            menu.removeItem(R.id.viewArtist)
        }
        if (song.album == TrackUtils.UNKNOWN_ALBUM){
            menu.removeItem(R.id.viewAlbum)
        }
    }

}