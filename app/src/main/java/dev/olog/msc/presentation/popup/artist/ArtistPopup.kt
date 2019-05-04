package dev.olog.msc.presentation.popup.artist

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.shared.TrackUtils

@Suppress("UNUSED_PARAMETER")
class ArtistPopup (
        view: View,
        artist: Artist,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_artist)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null){
            menu.removeItem(R.id.viewArtist)

            if (song.album == TrackUtils.UNKNOWN){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}