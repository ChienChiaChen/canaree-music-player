package dev.olog.msc.musicservice.interfaces

import dev.olog.msc.musicservice.model.PlayerMediaEntity

internal interface Player : PlayerLifecycle {

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    fun prepare(playerModel: PlayerMediaEntity)
    fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType)
    fun play(playerModel: PlayerMediaEntity)

    fun resume()
    fun pause(stopService: Boolean, releaseFocus: Boolean = true)
    fun seekTo(millis: Long)

    fun forwardTenSeconds()
    fun replayTenSeconds()

    fun forwardThirtySeconds()
    fun replayThirtySeconds()

    fun stopService()

    fun setVolume(volume: Float)
}

internal enum class SkipType {
    NONE,
    SKIP_PREVIOUS,
    SKIP_NEXT,
    TRACK_ENDED
}
