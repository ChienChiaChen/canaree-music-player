package dev.olog.msc.presentation.base.media

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.DefaultLifecycleObserver
import dev.olog.msc.presentation.base.activity.MusicGlueActivity
import dev.olog.msc.shared.MusicServiceConnectionState

class MusicServiceConnection(
    private val activity: MusicGlueActivity

) : MediaBrowserCompat.ConnectionCallback(), DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

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