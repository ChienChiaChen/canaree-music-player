package dev.olog.msc.musicservice.focus

import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import dagger.Lazy
import dev.olog.msc.musicservice.interfaces.Player
import dev.olog.msc.musicservice.volume.IPlayerVolume
import javax.inject.Inject

private enum class FocusState {
    NONE, PLAY_WHEN_READY, DELAYED, GAIN;
}

internal class AudioFocusBehavior @Inject constructor(
    private val player: Lazy<Player>,
    private val volume: IPlayerVolume,
    private val audioManager: AudioManager

) : AudioManager.OnAudioFocusChangeListener {

    private val focusRequest by lazy { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    private val focusLock = Object()

    internal fun requestFocus(): Boolean {
        val focus = requestFocusInternal()
        currentFocus = when (focus) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> FocusState.GAIN
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> FocusState.DELAYED
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> FocusState.NONE
            else -> throw IllegalStateException("audio focus response not handle with code $focus")
        }
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    internal fun abandonFocus() {
        currentFocus = FocusState.NONE
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
    }

    private fun requestFocusInternal(): Int {
        return AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
    }

    private fun buildFocusRequest(): AudioFocusRequestCompat {

        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(this)
            .setWillPauseWhenDucked(false) // not pause but providing my implementation
//            .setAcceptsDelayedFocusGain(true) TODO
            .setAudioAttributes(
                AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                    .build()
            )
            .build()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        synchronized(focusLock) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    player.get().setVolume(this.volume.normal())
                    if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED) {
                        player.get().onResume()
                    }
                    currentFocus = FocusState.GAIN
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    currentFocus = FocusState.NONE
                    player.get().onPause(false, releaseFocus = true)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (player.get().isPlaying()) {
                        currentFocus = FocusState.PLAY_WHEN_READY
                    }
                    player.get().onPause(false, releaseFocus = currentFocus != FocusState.PLAY_WHEN_READY)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    player.get().setVolume(this.volume.ducked())
                }

            }
        }
    }
}