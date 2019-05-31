package dev.olog.msc.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.ImageViews
import dev.olog.msc.presentation.base.extensions.isCollapsed
import dev.olog.msc.presentation.base.extensions.isExpanded
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.ObservableAdapter
import dev.olog.msc.presentation.base.list.drag.OnStartDragListener
import dev.olog.msc.presentation.base.list.drag.TouchableAdapter
import dev.olog.msc.presentation.base.list.extensions.elevateSongOnTouch
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.widgets.SwipeableView
import dev.olog.msc.presentation.media.*
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.player.animation.rotate
import dev.olog.msc.presentation.player.appearance.IPlayerAppearanceDelegate
import dev.olog.msc.shared.ui.extensions.*
import dev.olog.msc.shared.ui.theme.ImageShape
import dev.olog.msc.shared.ui.theme.playerTheme
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.player_controls.view.*

class PlayerFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val appearanceDelegate: IPlayerAppearanceDelegate,
    private val onStartDragListener: OnStartDragListener

) : ObservableAdapter<DisplayableItem>(lifecycle), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_mini_queue -> {
                viewHolder.itemView.setOnClickListener {
                    val item = getItem(viewHolder.adapterPosition)
                    mediaProvider.skipToQueueItem(item.trackNumber.toLong())
                }
                viewHolder.itemView.setOnLongClickListener {
                    val item = getItem(viewHolder.adapterPosition)
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                    true
                }
                viewHolder.itemView.findViewById<View>(R.id.more)?.setOnClickListener { view ->
                    val item = getItem(viewHolder.adapterPosition)
                    navigator.toDialog(item.mediaId, view)
                }
                viewHolder.elevateSongOnTouch()

                viewHolder.itemView.findViewById<View>(R.id.dragHandle).setOnTouchListener { v, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        onStartDragListener.onStartDrag(viewHolder)
                    }
                    false
                }
            }
            R.layout.item_playing_queue_load_more -> { /* do nothing */
            }
            else -> {
                // for each player
                viewHolder.itemView.findViewById<View>(R.id.more)?.setOnClickListener { view ->
                    val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                    navigator.toDialog(mediaId, view)
                }
                val view = viewHolder.itemView
                view.bigCover.observeProcessorColors().subscribe(viewHolder, viewModel::updateProcessorColors)
                view.bigCover.observePaletteColors().subscribe(viewHolder, viewModel::updatePaletteColors)
            }
        }

    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)

        val view = holder.itemView
        if (ImageViews.IMAGE_SHAPE == ImageShape.RECTANGLE) { // TODO check
            view.coverWrapper?.radius = 0f
        }
        val viewType = holder.itemViewType
        when (viewType){
            R.layout.fragment_player_controls,
            R.layout.fragment_player_controls_spotify,
            R.layout.fragment_player_controls_flat,
            R.layout.fragment_player_controls_big_image,
            R.layout.fragment_player_controls_fullscreen,
            R.layout.fragment_player_controls_clean,
            R.layout.fragment_player_controls_mini -> {
                bindPlayerControls(view, holder)
                appearanceDelegate.initViewHolderListeners(holder, viewType)
            }
        }
    }

    private fun bindPlayerControls(view: View, holder: DataBoundViewHolder) {

        mediaProvider.observeMetadata()
            .subscribe(holder) {
                viewModel.updateCurrentTrackId(it.getId())
                updateMetadata(view, it)
                updateImage(view, it)
            }

        mediaProvider.observePlaybackState()
            .subscribe(holder) { onPlaybackStateChanged(view, it) }

        view.seekBar.setListener(
            onProgressChanged = {
                view.bookmark.text = TextUtils.formatMillis(it)
            }, onStartTouch = {

            }, onStopTouch = {
                mediaProvider.seekTo(it.toLong())
            })

        viewModel.observeProgress
            .subscribe(holder) { view.seekBar.setProgress(it) }

        if (view.repeat != null) {
            mediaProvider.observeRepeat()
                .subscribe(holder, view.repeat::cycle)
            view.repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
        }
        if (view.shuffle != null) {
            mediaProvider.observeShuffle()
                .subscribe(holder, view.shuffle::cycle)

            view.shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
        }

        view.favorite.setOnClickListener { mediaProvider.togglePlayerFavorite() }

        view.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener {
            override fun onSwipedLeft() {
                mediaProvider.skipToNext()
            }

            override fun onSwipedRight() {
                mediaProvider.skipToPrevious()
            }

            override fun onClick() {
                mediaProvider.playPause()
            }

            override fun onLeftEdgeClick() {
                mediaProvider.skipToPrevious()
            }

            override fun onRightEdgeClick() {
                mediaProvider.skipToNext()
            }
        })

        viewModel.onFavoriteStateChanged
            .subscribe(holder, view.favorite::onNextState)

        view.lyrics.setOnClickListener {
            val activity = view.context as FragmentActivity
            navigator.toOfflineLyrics(activity)
        }

        val replayView = view.findViewById<View>(R.id.replay)
        replayView.setOnClickListener {
            replayView.rotate(-30)
            mediaProvider.replayTenSeconds()
        }

        val replay30View = view.findViewById<View>(R.id.replay30)
        replay30View.setOnClickListener {
            replay30View.rotate(-50)
            mediaProvider.replayThirtySeconds()
        }

        val forwardView = view.findViewById<View>(R.id.forward)
        forwardView.setOnClickListener {
            forwardView.rotate(30)
            mediaProvider.forwardTenSeconds()
        }

        val forward30View = view.findViewById<View>(R.id.forward30)
        forward30View.setOnClickListener {
            forward30View.rotate(50)
            mediaProvider.forwardThirtySeconds()
        }

        val playbackSpeed = view.findViewById<View>(R.id.playbackSpeed)
        playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(playbackSpeed) }

        val context = view.context

        mediaProvider.observePlaybackState()
            .map { it.state }
            .filter { state ->
                state == STATE_SKIPPING_TO_NEXT || state == STATE_SKIPPING_TO_PREVIOUS
            }
            .map { state -> state == STATE_SKIPPING_TO_NEXT }
            .subscribe(holder) { animateSkipTo(view, it) }

        mediaProvider.observePlaybackState()
            .map { it.state }
            .filter { it == STATE_PLAYING || it == STATE_PAUSED }
            .distinctUntilChanged()
            .subscribe(holder) { state ->
                if (state == STATE_PLAYING) {
                    playAnimation(view, true)
                } else {
                    pauseAnimation(view, true)
                }
            }

        view.next.setOnClickListener { mediaProvider.skipToNext() }
        view.playPause.setOnClickListener { mediaProvider.playPause() }
        view.previous.setOnClickListener { mediaProvider.skipToPrevious() }

        viewModel.observePlayerControlsVisibility()
            .filter { !context.playerTheme().isFullscreen() && !context.playerTheme().isMini() }
            .subscribe(holder) { visible ->
                view.previous.toggleVisibility(visible, true)
                view.playPause.toggleVisibility(visible, true)
                view.next.toggleVisibility(visible, true)
            }

        viewModel.skipToNextVisibility
            .subscribe(holder, view.next::updateVisibility)

        viewModel.skipToPreviousVisibility
            .subscribe(holder, view.previous::updateVisibility)
    }

    private fun updateMetadata(view: View, metadata: MediaMetadataCompat) {
        view.title.text = metadata.getTitle()
        view.artist.text = metadata.getArtist()

        val duration = metadata.getDuration()

        val readableDuration = metadata.getDurationReadable()
        view.duration.text = readableDuration
        view.seekBar.max = duration.toInt()

        val isPodcast = metadata.isPodcast()
        val playerControlsRoot: ConstraintLayout = view.findViewById(R.id.playerControls)
            ?: view.findViewById(R.id.playerRoot) as ConstraintLayout
        playerControlsRoot.findViewById<View>(R.id.replay).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.replay30).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward30).toggleVisibility(isPodcast, true)
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat) {
        view.bigCover?.loadImage(metadata) ?: return
    }

    private fun openPlaybackSpeedPopup(view: View) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.dialog_playback_speed)
        popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
        popup.setOnMenuItemClickListener {
            viewModel.setPlaybackSpeed(it.itemId)
            true
        }
        popup.show()
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat) {
        val isPlaying = playbackState.isPlaying()
        if (isPlaying || playbackState.isPaused()) {
            view.nowPlaying?.isActivated = isPlaying
            if (view.context.playerTheme().isClean()) {
                view.bigCover?.isActivated = isPlaying
            } else {
                view.coverWrapper?.isActivated = isPlaying
            }

        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPlay(isPanelExpanded && animate)
    }

    private fun pauseAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPause(isPanelExpanded && animate)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun onMoved(from: Int, to: Int) {
        val headers = 1
        mediaProvider.swapRelative(from - headers, to - headers)
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val headers = 1
        mediaProvider.removeRelative(viewHolder.adapterPosition - headers)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_mini_queue
    }

    override fun onClearView() {

    }
}