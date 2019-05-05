package dev.olog.msc.musicservice

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import javax.inject.Inject

@PerService
internal class ShuffleMode @Inject constructor(
        private val mediaSession: MediaSessionCompat,
        private val musicPreferencesUseCase: MusicPreferencesGateway
) {

    init {
        mediaSession.setShuffleMode(getState())
    }

    fun isEnabled(): Boolean= getState() != SHUFFLE_MODE_NONE

    fun setEnabled(enabled: Boolean){
        val shuffleMode = if (enabled) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE
        musicPreferencesUseCase.setShuffleMode(shuffleMode)
        mediaSession.setShuffleMode(shuffleMode)
    }

    fun getState(): Int = musicPreferencesUseCase.getShuffleMode()

    /**
     * @return true if new shuffle state is enabled
     */
    fun update(): Boolean {
        val shuffleMode = getState()

        val newState = if (shuffleMode == SHUFFLE_MODE_NONE) {
            SHUFFLE_MODE_ALL
        } else {
            SHUFFLE_MODE_NONE
        }

        musicPreferencesUseCase.setShuffleMode(newState)
        mediaSession.setShuffleMode(newState)

        return newState != SHUFFLE_MODE_NONE
    }

}
