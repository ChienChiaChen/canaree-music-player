package dev.olog.msc.musicservice.equalizer

interface IBassBoost {

    fun getStrength(): Int
    fun setStrength(value: Int)

    fun onAudioSessionIdChanged(audioSessionId: Int)

    fun setEnabled(enabled: Boolean)

    fun release()

}