package dev.olog.msc.floatingwindowservice

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.floatingwindowservice.music.service.MusicServiceBinder
import dev.olog.msc.shared.MusicConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.shared.core.coroutines.DefaultScope
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.extensions.isPlaying
import dev.olog.msc.shared.ui.extensions.subscribe
import dev.olog.msc.shared.ui.playpause.IPlayPauseBehavior
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

internal class LyricsContent(
    context: Context,
    private val musicServiceBinder: MusicServiceBinder

) : WebViewContent(context, R.layout.content_web_view_with_player), DefaultLifecycleObserver,
    CoroutineScope by DefaultScope() {

    private val playPauseBehavior = content.findViewById<ImageButton>(R.id.playPause) as IPlayPauseBehavior
    private val playPause = content.findViewById<ImageButton>(R.id.playPause)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val title = content.findViewById<TextView>(R.id.header)
    private val artist = content.findViewById<TextView>(R.id.subHeader)

    private var seekBarJob: Job? = null

    init {
        lifecycle.addObserver(this)
        playPause.setOnClickListener { musicServiceBinder.playPause() }

        launch {
            musicServiceBinder.onStateChanged()
        }
        musicServiceBinder.onStateChanged()
            .subscribe(this) {
                handleSeekBarState(it.isPlaying(), it.playbackSpeed)
            }

        musicServiceBinder.animatePlayPauseLiveData
            .subscribe(this) {
                if (it == PlaybackStateCompat.STATE_PLAYING) {
                    playPauseBehavior.animationPlay(true)
                } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                    playPauseBehavior.animationPause(true)
                }
            }

        musicServiceBinder.onMetadataChanged
            .subscribe(this) {
                title.text = it.title
                artist.text = it.artist
            }

        musicServiceBinder.onBookmarkChangedLiveData
            .subscribe(this, this::updateProgressBarProgress)

        musicServiceBinder.onMaxChangedLiveData
            .subscribe(this, this::updateProgressBarMax)

        setupSeekBar()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        seekBar.setOnSeekBarChangeListener(null)
        playPause.setOnClickListener(null)
        cancel()
        seekBarJob?.cancel()
    }

    private fun updateProgressBarProgress(progress: Long) {
        seekBar.progress = progress.toInt()
    }

    private fun updateProgressBarMax(max: Long) {
        seekBar.max = max.toInt()
    }

    private fun handleSeekBarState(isPlaying: Boolean, speed: Float) {
        seekBarJob?.cancel()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun resumeSeekBar(speed: Float) {
        seekBarJob = launch(Dispatchers.Main) {
            flowInterval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .collect {
                    seekBar.incrementProgressBy((PROGRESS_BAR_INTERVAL * speed).toInt())
                }
        }
    }

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                musicServiceBinder.seekTo(seekBar.progress.toLong())
            }
        })
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}