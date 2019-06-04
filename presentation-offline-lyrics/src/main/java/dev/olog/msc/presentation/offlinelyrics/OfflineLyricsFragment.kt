package dev.olog.msc.presentation.offlinelyrics

import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import dev.olog.msc.offlinelyrics.EditLyricsDialog
import dev.olog.msc.offlinelyrics.NoScrollTouchListener
import dev.olog.msc.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.msc.presentation.base.extensions.*
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.DrawsOnTop
import dev.olog.msc.presentation.media.*
import dev.olog.msc.presentation.offlinelyrics.di.inject
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.extensions.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OfflineLyricsFragment : BaseFragment(), DrawsOnTop, CoroutineScope by MainScope() {

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter

    private val mediaProvider by lazyFast { act as MediaProvider }

    private var seekbarUpdateJob: Job? = null

    override fun injectComponent() {
        inject()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        postponeEnterTransition()
        view.image.post { startPostponedEnterTransition() }

        if (presenter.canShowLyricsTutorial()) {
            Tutorial.addLyrics(view.search, view.edit, view.sync)
        }

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                presenter.updateCurrentTrackId(it.getId())
                presenter.updateCurrentMetadata(it.getTitle().toString(), it.getArtist().toString())
                image.loadImage(it)
                header.text = it.getTitle()
                subHeader.text = it.getArtist()
                seekBar.max = it.getDuration().toInt()
            }

        launch(Dispatchers.Default) {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(ctx, seekBar.progress, it) }
                .map { text.precomputeText(it) }
                .collect {
                    withContext(Dispatchers.Main) {
                        emptyState.toggleVisibility(it.isEmpty(), true)
                        text.text = it
                    }
                }
        }

        mediaProvider.observePlaybackState()
            .filter { it.state == PlaybackState.STATE_PLAYING || it.state == PlaybackState.STATE_PAUSED }
            .subscribe(viewLifecycleOwner) {
                val isPlaying = it.state == PlaybackState.STATE_PLAYING
                seekBar.progress = it.position.toInt()
                handleSeekBarState(isPlaying, it.playbackSpeed)
            }

        view.image.observePaletteColors()
            .map { it.accent }
            .subscribe(viewLifecycleOwner) { accent ->
                subHeader.animateTextColor(accent)
                edit.animateBackgroundColor(accent)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        seekbarUpdateJob?.cancel()
        cancel()
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            EditLyricsDialog.show(act, presenter.getOriginalLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        fakeNext.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.playPause() })
        seekBar.setOnSeekBarChangeListener(seekBarListener)

        sync.setOnClickListener { _ ->
            OfflineLyricsSyncAdjustementDialog.show(ctx, presenter.getSyncAdjustement()) {
                presenter.updateSyncAdjustement(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        edit.setOnClickListener(null)
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        act.window.setLightStatusBar()

        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)
        seekBar.setOnSeekBarChangeListener(null)
        sync.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun searchLyrics() {
//        val toolbarColor = if (context.isWhite()) R.color.toolbar else R.color.theme_dark_toolbar TODO set color in res
        val toolbarColor = R.color.toolbar
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(ContextCompat.getColor(ctx, toolbarColor))
            .build()
        CustomTabsHelper.addKeepAliveExtra(ctx, customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(ctx, customTabIntent, uri) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (act.packageManager.isIntentSafe(intent)) {
                startActivity(intent)
            } else {
                act.toast(R.string.common_browser_not_found)
            }
        }
    }


    private fun handleSeekBarState(isPlaying: Boolean, speed: Float) {
        seekbarUpdateJob?.cancel()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun resumeSeekBar(speed: Float) {
        seekbarUpdateJob = launch {
            flowInterval(MusicConstants.PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .collect {
                    seekBar.incrementProgressBy((MusicConstants.PROGRESS_BAR_INTERVAL * speed).toInt())
                }
        }
    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            mediaProvider.seekTo(seekBar.progress.toLong())
        }
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}