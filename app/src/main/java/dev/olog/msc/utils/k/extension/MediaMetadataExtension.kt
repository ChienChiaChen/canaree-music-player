package dev.olog.msc.utils.k.extension

import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.core.MediaId
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.utils.TextUtils

fun MediaMetadataCompat.getTitle(): CharSequence {
    return getText(MediaMetadataCompat.METADATA_KEY_TITLE)
}

fun MediaMetadataCompat.getArtist(): CharSequence {
    val artist = getText(MediaMetadataCompat.METADATA_KEY_ARTIST)
    return TrackUtils.adjustArtist(artist.toString())
}

fun MediaMetadataCompat.getAlbum(): CharSequence {
    val album = getText(MediaMetadataCompat.METADATA_KEY_ALBUM)
    return TrackUtils.adjustAlbum(album.toString())
}

fun MediaMetadataCompat.getDuration(): Long {
    return getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
}

fun MediaMetadataCompat.getDurationReadable(): String {
    val duration = getDuration()
    return TextUtils.formatMillis(duration)
}

fun MediaMetadataCompat.getImage(): String {
    return getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
}

fun MediaMetadataCompat.getMediaId(): MediaId {
    val mediaId = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    return MediaId.fromString(mediaId)
}

fun MediaMetadataCompat.getId(): Long {
    return getMediaId().leaf!!
}

fun MediaMetadataCompat.isPodcast(): Boolean {
    return getLong(MusicConstants.IS_PODCAST) != 0L
}