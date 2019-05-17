package dev.olog.msc.floatingwindowservice.music.service

import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.core.MediaId
import dev.olog.msc.shared.MusicConstants

internal fun MediaMetadataCompat.getTitle(): CharSequence {
    return getText(MediaMetadataCompat.METADATA_KEY_TITLE)
}

internal fun MediaMetadataCompat.getArtist(): CharSequence {
    val artist = getText(MediaMetadataCompat.METADATA_KEY_ARTIST)
    return artist.toString()
}

internal fun MediaMetadataCompat.getDuration(): Long {
    return getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
}

internal fun MediaMetadataCompat.getImage(): String {
    return getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
}

internal fun MediaMetadataCompat.getMediaId(): MediaId {
    val mediaId = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    return MediaId.fromString(mediaId)
}

internal fun MediaMetadataCompat.getId(): Long {
    return getMediaId().leaf!!
}

internal fun MediaMetadataCompat.isPodcast(): Boolean {
    return getLong(MusicConstants.IS_PODCAST) != 0L
}