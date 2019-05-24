package dev.olog.msc.core.gateway.prefs

import dev.olog.msc.core.entity.LastMetadata
import kotlinx.coroutines.flow.Flow

interface MusicPreferencesGateway {

    fun getBookmark(): Long
    fun setBookmark(bookmark: Long)

    fun getLastIdInPlaylist(): Int
    fun setLastIdInPlaylist(idInPlaylist: Int)
    fun observeLastIdInPlaylist(): Flow<Int>

    fun getRepeatMode(): Int
    fun setRepeatMode(repeatMode: Int)

    fun getShuffleMode(): Int
    fun setShuffleMode(shuffleMode: Int)

    fun setSkipToPreviousVisibility(visible: Boolean)
    fun observeSkipToPreviousVisibility(): Flow<Boolean>

    fun setSkipToNextVisibility(visible: Boolean)
    fun observeSkipToNextVisibility(): Flow<Boolean>

    fun isMidnightMode(): Flow<Boolean>

    fun getLastMetadata(): LastMetadata
    fun setLastMetadata(metadata: LastMetadata)
    fun observeLastMetadata(): Flow<LastMetadata>

    fun setDefault()

    fun observeCrossFade(): Flow<Int>
    fun observeGapless(): Flow<Boolean>

    fun observePlaybackSpeed(): Flow<Float>
    fun setPlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float

    fun setLastPositionInQueue(position: Int)
    fun observeLastPositionInQueue(): Flow<Int>
    fun getLastPositionInQueue(): Int

}