package dev.olog.msc.musicservice.equalizer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.core.equalizer.IBassBoost
import dev.olog.msc.core.equalizer.IEqualizer
import dev.olog.msc.core.equalizer.IVirtualizer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@PerService
internal class OnAudioSessionIdChangeListener @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val equalizer: IEqualizer,
        private val virtualizer: IVirtualizer,
        private val bassBoost: IBassBoost

) : AudioRendererEventListener, DefaultLifecycleObserver {

    private var job: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job?.cancel()
    }

    override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {}

    override fun onAudioEnabled(counters: DecoderCounters?) {}

    override fun onAudioInputFormatChanged(format: Format?) {}

    override fun onAudioDecoderInitialized(decoderName: String?, initializedTimestampMs: Long, initializationDurationMs: Long) {}

    override fun onAudioDisabled(counters: DecoderCounters?) {}

    override fun onAudioSessionId(audioSessionId: Int) {
        job = GlobalScope.launch {
            delay(500)
            onAudioSessionIdInternal(audioSessionId)
        }
    }

    private fun onAudioSessionIdInternal(audioSessionId: Int){
        equalizer.onAudioSessionIdChanged(audioSessionId)
        virtualizer.onAudioSessionIdChanged(audioSessionId)
        bassBoost.onAudioSessionIdChanged(audioSessionId)
    }

    fun release(){
        // TODO why not called?
        equalizer.release()
        virtualizer.release()
        bassBoost.release()
    }
}