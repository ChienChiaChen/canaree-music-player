package dev.olog.msc.presentation.popup.podcastartist

import android.view.View

import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.shared.TrackUtils

internal class PodcastArtistPopup (
        view: View,
        artist: PodcastArtist,
        song: Podcast?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_podcast_artist)
        } else {
            inflate(R.menu.dialog_podcast)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null){
            menu.removeItem(R.id.viewArtist)

            if (song.album == TrackUtils.UNKNOWN_ALBUM){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}