package dev.olog.msc.musicservice.equalizer

interface IVirtualizer {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun release()

}