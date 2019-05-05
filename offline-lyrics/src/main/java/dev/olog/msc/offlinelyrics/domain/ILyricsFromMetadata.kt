package dev.olog.msc.offlinelyrics.domain

import dev.olog.msc.core.entity.track.Song

interface ILyricsFromMetadata{
    fun getLyrics(song: Song): String
    fun setLyrics(song: Song, lyrics: String)
}