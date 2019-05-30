package dev.olog.msc.presentation.media.connection

internal interface IMediaConnectionCallback {
    fun onConnectionStateChanged(state: MusicServiceConnectionState)
}