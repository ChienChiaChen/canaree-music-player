package dev.olog.msc.musicservice.player.media.source

import com.google.android.exoplayer2.source.MediaSource

internal interface SourceFactory <T>{
    fun get(model: T) : MediaSource
}