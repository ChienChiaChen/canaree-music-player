package dev.olog.msc.musicservice.interfaces

import dev.olog.msc.musicservice.ActionManager
import dev.olog.msc.musicservice.model.PlayerMediaEntity

internal interface Player : PlayerLifecycle, ActionManager.Callback {

    fun isPlaying(): Boolean
    fun getBookmark(): Long

    override fun onPrepare(playerModel: PlayerMediaEntity)
    override fun onPlayNext(playerModel: PlayerMediaEntity, skipType: SkipType)
    override fun onPlay(playerModel: PlayerMediaEntity)

    override fun onResume()
    override fun onPause(stopService: Boolean, releaseFocus: Boolean)
    override fun onSeek(millis: Long)

    override fun onReplayBy(seconds: Int)
    override fun onForwardBy(seconds: Int)

    fun stopService()

    fun setVolume(volume: Float)
}

internal enum class SkipType {
    NONE,
    SKIP_PREVIOUS,
    SKIP_NEXT,
    TRACK_ENDED
}
