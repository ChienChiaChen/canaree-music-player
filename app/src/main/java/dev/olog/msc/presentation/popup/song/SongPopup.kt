package dev.olog.msc.presentation.popup.song

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.core.entity.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener

class SongPopup(
        view: View,
        song: Song,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_song)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song.artist == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }
        if (song.album == AppConstants.UNKNOWN){
            menu.removeItem(R.id.viewAlbum)
        }
    }

}