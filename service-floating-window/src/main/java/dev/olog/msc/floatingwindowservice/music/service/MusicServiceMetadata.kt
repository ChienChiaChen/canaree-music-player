package dev.olog.msc.floatingwindowservice.music.service

import dev.olog.msc.imageprovider.ImageModel

internal data class MusicServiceMetadata(
        val id: Long,
        val title: String,
        val artist: String,
        val image: ImageModel,
        val duration: Long,
        val isPodcast: Boolean
)