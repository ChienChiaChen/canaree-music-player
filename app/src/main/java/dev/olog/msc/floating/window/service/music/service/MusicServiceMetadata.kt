package dev.olog.msc.floating.window.service.music.service

data class MusicServiceMetadata(
        val id: Long,
        val title: String,
        val artist: String,
        val duration: Long,
        val isPodcast: Boolean
)