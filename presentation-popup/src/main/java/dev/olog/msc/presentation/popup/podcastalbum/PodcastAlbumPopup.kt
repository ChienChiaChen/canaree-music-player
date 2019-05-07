package dev.olog.msc.presentation.popup.podcastalbum

import android.view.View

import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.shared.TrackUtils

class PodcastAlbumPopup(
        view: View,
        album: PodcastAlbum,
        song: Podcast?,
        listener: AbsPopupListener

) : AbsPopup(view)  {

    init {
        if (song == null){
            inflate(R.menu.dialog_podcast_album)
        } else {
            inflate(R.menu.dialog_podcast)
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