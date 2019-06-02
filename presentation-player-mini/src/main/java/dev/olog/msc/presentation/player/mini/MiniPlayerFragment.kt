package dev.olog.msc.presentation.player.mini

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.math.MathUtils
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.presentation.base.extensions.*
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.media.*
import dev.olog.msc.presentation.player.mini.di.inject
import dev.olog.msc.shared.MusicConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.shared.core.flow.flowInterval
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.ui.extensions.*
import dev.olog.msc.shared.ui.theme.miniPlayerTheme
import dev.olog.msc.shared.ui.theme.playerTheme
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.android.synthetic.main.fragment_mini_player.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment(), CoroutineScope by MainScope() {

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<MiniPlayerFragmentViewModel>(factory) }

    private var seekBarJob: Job? = null

    private val mediaProvider by lazyFast { activity as MediaProvider }

    override fun injectComponent() {
        inject()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }


        val (modelTitle, modelSubtitle) = viewModel.getMetadata()
        view.title.text = modelTitle
        view.artist.text = modelSubtitle

        setupMiniPlayerTheme(view)

        view.coverWrapper.toggleVisibility(requireContext().playerTheme().isMini(), true)

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                title.text = it.getTitle()
                viewModel.startShowingLeftTime(it.isPodcast(), it.getDuration())
                if (!it.isPodcast()) {
                    artist.text = it.getArtist()
                }
                updateProgressBarMax(it.getDuration())
                updateImage(it)
            }

        viewModel.observeProgress
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { text -> view.artist.text != text }
            .subscribe(viewLifecycleOwner) {
                artist.text = it
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlaying() || it.isPaused() }
            .distinctUntilChanged()
            .subscribe(viewLifecycleOwner) {
                updateProgressBarProgress(it.position)
                handleProgressBar(it.isPlaying(), it.playbackSpeed)
            }

        mediaProvider.observePlaybackState()
            .map { it.state }
            .filter { it == PlaybackStateCompat.STATE_PLAYING || it == PlaybackStateCompat.STATE_PAUSED }
            .distinctUntilChanged()
            .subscribe(viewLifecycleOwner) { state ->

                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    playAnimation(true)
                } else {
                    pauseAnimation(true)
                }
            }

        mediaProvider.observePlaybackState()
            .map { it.state }
            .filter { state ->
                state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                        state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
            }
            .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
            .subscribe(viewLifecycleOwner, this::animateSkipTo)

        viewModel.skipToNextVisibility
            .subscribe(viewLifecycleOwner) {
                view.next.updateVisibility(it)
            }

        viewModel.skipToPreviousVisibility
            .subscribe(viewLifecycleOwner) {
                view.previous.updateVisibility(it)
            }
    }

    private fun setupMiniPlayerTheme(view: View){
        if (ctx.miniPlayerTheme().isBlurry()){
            view.root.background = null
        }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(slidingPanelListener)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.toggleVisibility(!getSlidingPanel().isExpanded(), true)
        next.setOnClickListener { mediaProvider.skipToNext() }
        previous.setOnClickListener { mediaProvider.skipToPrevious() }
        playPause.setOnClickListener { mediaProvider.playPause() }
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removePanelSlideListener(slidingPanelListener)
        view?.setOnClickListener(null)
        next.setOnClickListener(null)
        previous.setOnClickListener(null)
        playPause.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        seekBarJob?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation(animate: Boolean) {
        playPause.animationPlay(getSlidingPanel().isCollapsed() && animate)
    }

    private fun pauseAnimation(animate: Boolean) {
        playPause.animationPause(getSlidingPanel().isCollapsed() && animate)
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            view!!.next.playAnimation()
        } else {
            view!!.previous.playAnimation()
        }
    }

    private fun updateProgressBarProgress(progress: Long) {
        view!!.progressBar.progress = progress.toInt()
    }

    private fun updateProgressBarMax(max: Long) {
        view!!.progressBar.max = max.toInt()
    }

    private fun updateImage(metadata: MediaMetadataCompat) {
        if (!requireContext().playerTheme().isMini()) {
            return
        }
        bigCover.loadImage(metadata)
    }

    private fun handleProgressBar(isPlaying: Boolean, speed: Float) {
        seekBarJob?.cancel()
        if (isPlaying) {
            resumeProgressBar(speed)
        }
    }

    private fun resumeProgressBar(speed: Float) {
        seekBarJob = launch {
            flowInterval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .collect {
                    progressBar.incrementProgressBy((PROGRESS_BAR_INTERVAL * speed).toInt())
                    viewModel.updateProgress((progressBar.progress + (PROGRESS_BAR_INTERVAL * speed)).toLong())
                }
        }
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            view?.toggleVisibility(slideOffset <= .8f, true)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}