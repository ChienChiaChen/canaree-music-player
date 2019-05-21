package dev.olog.msc.presentation.player.appearance

import android.content.res.ColorStateList
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.extensions.subscribe
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.utils.getDuration
import dev.olog.msc.presentation.player.PlayerFragmentViewModel
import dev.olog.msc.presentation.player.R
import dev.olog.msc.presentation.player.widgets.audiowave.AudioWaveViewWrapper
import dev.olog.msc.shared.MusicConstants
import dev.olog.msc.shared.ui.extensions.animateBackgroundColor
import dev.olog.msc.shared.ui.extensions.animateTextColor
import dev.olog.msc.shared.ui.theme.HasPlayerTheme
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.player_controls.view.*

interface IPlayerAppearanceDelegate {

    companion object {
        fun get(hasPlayerTheme: HasPlayerTheme, viewModel: PlayerFragmentViewModel): IPlayerAppearanceDelegate {
            return when {
                hasPlayerTheme.isDefault() -> AppearanceDefault(viewModel)
                hasPlayerTheme.isFlat() -> AppearanceFlat(viewModel)
                hasPlayerTheme.isSpotify() -> AppearanceSpotify(viewModel)
                hasPlayerTheme.isFullscreen() -> AppearanceFullscreen(viewModel)
                hasPlayerTheme.isBigImage() -> AppearanceBigImage(viewModel)
                hasPlayerTheme.isClean() -> AppearanceClean(viewModel)
                hasPlayerTheme.isMini() -> AppearanceMini(viewModel)
                else -> throw IllegalArgumentException("invalid theme")
            }
        }
    }

    fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

}

class AppearanceDefault(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.artist.apply { animateTextColor(accent) }
                view.shuffle?.updateSelectedColor(accent)
                view.repeat?.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }
    }
}

class AppearanceFlat(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observeProcessorColors()
            .subscribe(viewHolder) { colors ->
                view.title.apply {
                    animateTextColor(colors.primaryText)
                    animateBackgroundColor(colors.background)
                }
                view.artist.apply {
                    animateTextColor(colors.secondaryText)
                    animateBackgroundColor(colors.background)
                }
            }
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.shuffle?.updateSelectedColor(accent)
                view.repeat?.updateSelectedColor(accent)
            }
    }
}

class AppearanceSpotify(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.artist.apply { animateTextColor(accent) }
                view.shuffle?.updateSelectedColor(accent)
                view.repeat?.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }
    }
}

class AppearanceFullscreen(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView

        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.artist.animateTextColor(accent)
                view.playPause.backgroundTintList = ColorStateList.valueOf(accent)
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
            }
    }
}

class AppearanceBigImage(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.artist.apply { animateTextColor(accent) }
                view.shuffle?.updateSelectedColor(accent)
                view.repeat?.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }

        val mediaProvider = view.context as MediaProvider
        val waveWrapper: AudioWaveViewWrapper = view.findViewById(R.id.waveWrapper)

        mediaProvider.onMetadataChanged()
            .subscribe(viewHolder) {
                waveWrapper.onTrackChanged(it.getString(MusicConstants.PATH))
                waveWrapper.updateMax(it.getDuration())
            }

        viewModel.observeProgress
            .subscribe(viewHolder) { waveWrapper.updateProgress(it) }
    }
}

class AppearanceClean(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.artist.apply { animateTextColor(accent) }
                view.shuffle?.updateSelectedColor(accent)
                view.repeat?.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
            }
    }
}

class AppearanceMini(private val viewModel: PlayerFragmentViewModel) : IPlayerAppearanceDelegate {
    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        val view = viewHolder.itemView
        viewModel.observePaletteColors()
            .subscribe(viewHolder) { palette ->
                val accent = palette.accent
                view.artist.apply { animateTextColor(accent) }
                view.shuffle.updateSelectedColor(accent)
                view.repeat.updateSelectedColor(accent)
                view.seekBar.apply {
                    thumbTintList = ColorStateList.valueOf(accent)
                    progressTintList = ColorStateList.valueOf(accent)
                }
                view.more.imageTintList = ColorStateList.valueOf(accent)
                view.lyrics.imageTintList = ColorStateList.valueOf(accent)
            }
    }
}