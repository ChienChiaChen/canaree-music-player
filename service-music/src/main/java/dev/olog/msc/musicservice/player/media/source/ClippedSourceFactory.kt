package dev.olog.msc.musicservice.player.media.source

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.musicservice.player.crossfade.CrossFadePlayerImpl
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class ClippedSourceFactory @Inject constructor (
        @ServiceLifecycle lifecycle: Lifecycle,
        private val sourceFactory: DefaultSourceFactory,
        musicPrefsUseCase: MusicPreferencesGateway

) : DefaultLifecycleObserver, SourceFactory<CrossFadePlayerImpl.Model> {

    companion object {
        private val clipStart = TimeUnit.SECONDS.toMicros(2)
        private val clipEnd = TimeUnit.SECONDS.toMicros(4)
    }

    // when gapless is on, clip mediaSource
    private var isGapless = false

    private val isGaplessDisposable = musicPrefsUseCase.observeGapless()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isGapless = it }, Throwable::printStackTrace)

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        isGaplessDisposable.unsubscribe()
    }


    /*
     * Clip the media source only when gapless is On,
     * otherwise fallback to default media source.
     * NB -> some Flac files are not seekable and clippable, and when clipped,
     *       an error is thrown, so flacs will never be clipped
     */
    override fun get(model: CrossFadePlayerImpl.Model): MediaSource {
        val mediaSource = sourceFactory.get(model.mediaEntity)
        val isFlac = model.isFlac

        if (!isFlac && isGapless && model.isGoodIdeaToClip && !model.mediaEntity.isPodcast){
            if (model.isTrackEnded){
                // clip start and end
                return ClippingMediaSource(mediaSource, clipStart, calculateEndClip(model.duration))
            }
            // skipTo case, clip only the end
            return ClippingMediaSource(mediaSource, 0, calculateEndClip(model.duration))

        }

        return mediaSource
    }

    private fun calculateEndClip(trackDuration: Long): Long {
        return TimeUnit.MILLISECONDS.toMicros(trackDuration) - clipEnd
    }

}