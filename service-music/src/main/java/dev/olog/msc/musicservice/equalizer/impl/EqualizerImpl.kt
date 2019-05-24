package dev.olog.msc.musicservice.equalizer.impl

import android.media.audiofx.Equalizer
import dev.olog.msc.core.equalizer.IEqualizer
import dev.olog.msc.core.gateway.prefs.EqualizerPreferencesGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class EqualizerImpl @Inject constructor(
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IEqualizer {

    private var equalizer: Equalizer? = null
    private val listeners = mutableListOf<IEqualizer.Listener>()

    private val availabilityPublisher = BroadcastChannel<Boolean>(Channel.CONFLATED)

    init {
        GlobalScope.launch { availabilityPublisher.send(true) }
    }

    override fun addListener(listener: IEqualizer.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IEqualizer.Listener) {
        listeners.remove(listener)
    }

    override fun getBandLevel(band: Int): Float {
        return useOrDefault({ equalizer!!.getBandLevel(band.toShort()).toFloat() }, 0f)
    }

    override fun setBandLevel(band: Int, level: Float) {
        use {
            equalizer!!.setBandLevel(band.toShort(), level.toShort())
        }
    }

    override fun setPreset(position: Int) {
        use {
            equalizer!!.usePreset(position.toShort())

            listeners.forEach {
                for (band in 0 until equalizer!!.numberOfBands) {
                    val level = equalizer!!.getBandLevel(band.toShort()) / 100
                    it.onPresetChange(band, level.toFloat())
                }
            }
        }
    }

    override fun getPresets(): List<String> {
        return useOrDefault({
            (0 until equalizer!!.numberOfPresets)
                .map { equalizer!!.getPresetName(it.toShort()) }
        }, emptyList())
    }

    override fun getCurrentPreset(): Int {
        return useOrDefault({ equalizer!!.currentPreset.toInt() }, 0)
    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
        release()

        use {
            equalizer = Equalizer(0, audioSessionId)
            equalizer!!.enabled = equalizerPrefsUseCase.isEqualizerEnabled()
        }

        try {
            val properties = equalizerPrefsUseCase.getEqualizerSettings()
            val settings = Equalizer.Settings(properties)
            equalizer!!.properties = settings
        } catch (ex: Exception) {
        }
    }

    override fun setEnabled(enabled: Boolean) {
        use {
            equalizer!!.enabled = enabled
        }
    }

    override fun release() {
        equalizer?.let {
            try {
                equalizerPrefsUseCase.saveEqualizerSettings(it.properties.toString())
            } catch (ex: Exception) {
            }
            use {
                it.release()
            }
        }
    }

    override fun isAvailable(): Flow<Boolean> = availabilityPublisher.asFlow().distinctUntilChanged()

    private fun use(action: () -> Unit) {
        try {
            action()
            GlobalScope.launch(Dispatchers.Main) { availabilityPublisher.send(true) }
        } catch (ex: Exception) {
            ex.printStackTrace()
            GlobalScope.launch(Dispatchers.Main) { availabilityPublisher.send(false) }
        }
    }

    private fun <T> useOrDefault(action: () -> T, default: T): T {
        return try {
            val v = action()
            GlobalScope.launch(Dispatchers.Main) { availabilityPublisher.send(true) }
            v
        } catch (ex: Exception) {
            ex.printStackTrace()
            GlobalScope.launch(Dispatchers.Main) { availabilityPublisher.send(false) }
            default
        }
    }

}