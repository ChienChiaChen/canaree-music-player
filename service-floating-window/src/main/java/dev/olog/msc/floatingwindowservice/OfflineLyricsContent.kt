package dev.olog.msc.floatingwindowservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.olog.msc.core.MediaId
import dev.olog.msc.floatingwindowservice.api.Content
import dev.olog.msc.floatingwindowservice.music.service.MusicServiceBinder
import dev.olog.msc.floatingwindowservice.music.service.MusicServiceMetadata
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.offlinelyrics.EditLyricsDialog
import dev.olog.msc.offlinelyrics.NoScrollTouchListener
import dev.olog.msc.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.extensions.isPlaying
import dev.olog.msc.shared.ui.extensions.animateBackgroundColor
import dev.olog.msc.shared.ui.extensions.animateTextColor
import dev.olog.msc.shared.ui.extensions.subscribe
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.msc.shared.ui.imageview.BlurImageView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

internal class OfflineLyricsContent(
    private val context: Context,
    private val musicServiceBinder: MusicServiceBinder,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var seekBarJob: Job? = null
    private var lyricsJob: Job? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val header = content.findViewById<TextView>(R.id.header)
    private val subHeader = content.findViewById<TextView>(R.id.subHeader)
    private val edit = content.findViewById<FloatingActionButton>(R.id.edit)
    private val sync = content.findViewById<ImageButton>(R.id.sync)
    private val lyricsText = content.findViewById<TextView>(R.id.text)
    private val image = content.findViewById<BlurImageView>(R.id.image)
    private val emptyState = content.findViewById<TextView>(R.id.emptyState)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val fakeNext = content.findViewById<View>(R.id.fakeNext)
    private val fakePrev = content.findViewById<View>(R.id.fakePrev)
    private val scrollView = content.findViewById<ScrollView>(R.id.scrollBar)

    private fun loadImage(metadata: MusicServiceMetadata) {
        val mediaId = metadata.mediaId
        Glide.with(context).clear(this.image)

        val drawable = CoverUtils.getGradient(
            context, if (metadata.isPodcast) MediaId.podcastId(metadata.id)
            else MediaId.songId(metadata.id)
        )

        Glide.with(context)
            .load(mediaId) // TODO is loading image?
            .placeholder(drawable)
            .priority(Priority.IMMEDIATE)
            .override(500)
            .into(this.image)
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()
        edit.setOnClickListener {
            EditLyricsDialog.showForService(context, presenter.getOriginalLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        sync.setOnClickListener {
            OfflineLyricsSyncAdjustementDialog.showForService(context, presenter.getSyncAdjustement()) {
                presenter.updateSyncAdjustement(it)
            }
        }
        fakeNext.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.playPause() })

        image.observePaletteColors()
            .observe(this, Observer { palette ->
                val accent = palette.accent
                edit.animateBackgroundColor(accent)
                subHeader.animateTextColor(accent)
            })

        musicServiceBinder.onMetadataChanged
            .subscribe(this) {
                presenter.updateCurrentTrackId(it.id)
                loadImage(it)
                header.text = it.title
                subHeader.text = it.artist
                updateProgressBarMax(it.duration)
            }

        musicServiceBinder.onStateChanged()
            .subscribe(this) {
                handleSeekBarState(it.isPlaying(), it.playbackSpeed)
            }

        musicServiceBinder.onBookmarkChangedLiveData
            .subscribe(this) { seekBar.progress = it.toInt() }

        lyricsJob = GlobalScope.launch {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(context, seekBar.progress, it) }
                .collect {
                    withContext(Dispatchers.Main){
                        emptyState.toggleVisibility(it.isEmpty(), true)
                        lyricsText.setText(it)
                    }
                }

        }

        setupSeekBar()
    }

    override fun onHidden() {
        super.onHidden()
        edit.setOnClickListener(null)
        sync.setOnClickListener(null)
        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)

        seekBarJob?.cancel()
        lyricsJob?.cancel()
        presenter.onDestroy()
    }

    private fun handleSeekBarState(isPlaying: Boolean, speed: Float) {
        seekBarJob?.cancel()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun updateProgressBarMax(max: Long) {
        seekBar.max = max.toInt()
    }

    private fun resumeSeekBar(speed: Float) {
        seekBarJob = GlobalScope.launch(Dispatchers.Main) {
            flowInterval(
                MusicConstants.PROGRESS_BAR_INTERVAL,
                TimeUnit.MILLISECONDS
            )
                .collect {
                    seekBar.incrementProgressBy((MusicConstants.PROGRESS_BAR_INTERVAL * speed).toInt())
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

}