package dev.olog.presentation.base.media

import android.support.v4.media.MediaBrowserCompat
import dev.olog.msc.shared.MusicServiceConnectionState
import dev.olog.presentation.base.activity.MusicGlueActivity

class MusicServiceConnection(
        private val activity: MusicGlueActivity

) : MediaBrowserCompat.ConnectionCallback() {

    override fun onConnected() {
        activity.updateConnectionState(MusicServiceConnectionState.CONNECTED)
    }

    override fun onConnectionSuspended() {
        activity.updateConnectionState(MusicServiceConnectionState.FAILED)
    }

    override fun onConnectionFailed() {
        activity.updateConnectionState(MusicServiceConnectionState.FAILED)
    }
}