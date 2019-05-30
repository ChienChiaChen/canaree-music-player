package dev.olog.msc.floatingwindowservice

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.olog.msc.floatingwindowservice.api.Content
import dev.olog.msc.floatingwindowservice.music.service.MusicGlueService
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.offlinelyrics.EditLyricsDialog
import dev.olog.msc.offlinelyrics.NoScrollTouchListener
import dev.olog.msc.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.msc.presentation.media.*
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.core.flow.flowInterval
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
    private val glueService: MusicGlueService,
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

    private fun loadImage(metadata: MediaMetadataCompat) {
        val mediaId = metadata.getMediaId()
        GlideApp.with(context).clear(this.image)

        val drawable = CoverUtils.getGradient(context, mediaId)

        GlideApp.with(context)
            .load(mediaId)
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
        fakeNext.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(context) { glueService.playPause() })

        image.observePaletteColors()
            .observe(this, Observer { palette ->
                val accent = palette.accent
                edit.animateBackgroundColor(accent)
                subHeader.animateTextColor(accent)
            })

        glueService.observeMetadata()
            .subscribe(this) {
                presenter.updateCurrentTrackId(it.getId())
                loadImage(it)
                header.text = it.getTitle()
                subHeader.text = it.getArtist()
                updateProgressBarMax(it.getDuration())
            }

        glueService.observePlaybackState()
            .subscribe(this) {
                handleSeekBarState(it.isPlaying(), it.playbackSpeed)
            }

        glueService.onBookmarkChangedLiveData
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
                glueService.seekTo(seekBar.progress.toLong())
            }
        })
    }

}