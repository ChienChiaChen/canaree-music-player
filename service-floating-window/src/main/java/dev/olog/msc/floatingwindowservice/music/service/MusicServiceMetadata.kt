package dev.olog.msc.floatingwindowservice.music.service

import dev.olog.msc.core.MediaId

internal data class MusicServiceMetadata(
        val id: Long,
        val title: String,
        val artist: String,
        val mediaId: MediaId,
        val duration: Long,
        val isPodcast: Boolean
)