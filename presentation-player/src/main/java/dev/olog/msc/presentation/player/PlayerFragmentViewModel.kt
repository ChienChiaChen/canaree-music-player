package dev.olog.msc.presentation.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.presentation.base.extensions.liveDataOf
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.theme.player.theme.*
import dev.olog.msc.shared.ui.imageview.adaptive.PaletteColors
import dev.olog.msc.shared.ui.imageview.adaptive.ProcessorColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val processorLiveData = liveDataOf<ProcessorColors>()
    private val paletteLiveData = liveDataOf<PaletteColors>()
    private val favoriteLiveData = liveDataOf<FavoriteEnum>()
    private val miniQueue = liveDataOf<List<DisplayableItem>>()
    private val currentTrackIdPublisher = liveDataOf<Long>()

    private val skipToNextLiveData = liveDataOf<Boolean>()
    private val skipToPreviousLiveData = liveDataOf<Boolean>()
    private val progressPublisher = liveDataOf<Int>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            observeFavoriteAnimationUseCase.execute()
                .distinctUntilChanged()
                .collect { favoriteLiveData.postValue((it)) }
        }
        viewModelScope.launch(Dispatchers.Default) {
            musicPrefsUseCase.observeSkipToNextVisibility()
                .distinctUntilChanged()
                .collect { skipToNextLiveData.postValue(it) }
        }
        viewModelScope.launch(Dispatchers.Default) {
            musicPrefsUseCase.observeSkipToPreviousVisibility()
                .distinctUntilChanged()
                .collect { skipToPreviousLiveData.postValue(it) }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    val onFavoriteStateChanged: LiveData<FavoriteEnum> = favoriteLiveData

    val skipToNextVisibility: LiveData<Boolean> = skipToNextLiveData

    val skipToPreviousVisibility: LiveData<Boolean> = skipToPreviousLiveData

    fun observeProcessorColors(): LiveData<ProcessorColors> = processorLiveData

    fun observePaletteColors(): LiveData<PaletteColors> = paletteLiveData

    fun updateProcessorColors(palette: ProcessorColors) {
        viewModelScope.launch(Dispatchers.Default) {
            if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                processorLiveData.postValue(palette)
            }
        }
    }

    fun updatePaletteColors(palette: PaletteColors) {
        viewModelScope.launch(Dispatchers.Default) {
            if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                paletteLiveData.postValue(palette)
            }
        }
    }

    fun getCurrentTrackId() = currentTrackIdPublisher.value!!

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.value = trackId
    }

    fun observeMiniQueue(): LiveData<List<DisplayableItem>> = miniQueue

    fun updateQueue(context: Context, queue: List<MediaSessionCompat.QueueItem>) {
        viewModelScope.launch(Dispatchers.Default) {
            if (context.isMini()) {
                miniQueue.postValue(listOf(playerControls()))
            } else {
                val copy = queue.map { it.toDisplayableItem() }.toMutableList()
                if (copy.size > PlayingQueueGateway.MINI_QUEUE_SIZE - 1) {
                    copy.add(footerLoadMore)
                }
                copy.add(0, playerControls())
                miniQueue.postValue(copy)
            }
        }
    }

    val observeProgress: LiveData<Int> = progressPublisher

    fun updateProgress(progress: Int) {
        progressPublisher.value = progress
    }

    private val footerLoadMore =
        DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    fun playerControls(): DisplayableItem {
        val id = when {
            context.isDefault() -> R.layout.fragment_player_controls
            context.isFlat() -> R.layout.fragment_player_controls_flat
            context.isSpotify() -> R.layout.fragment_player_controls_spotify
            context.isFullscreen() -> R.layout.fragment_player_controls_fullscreen
            context.isBigImage() -> R.layout.fragment_player_controls_big_image
            context.isClean() -> R.layout.fragment_player_controls_clean
            context.isMini() -> R.layout.fragment_player_controls_mini
            else -> throw IllegalStateException("invalid theme")
        }
        return DisplayableItem(id, MediaId.headerId("player controls id"), "")
    }

    fun showLyricsTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.canShowLyricsTutorial()
    }

    fun getPlaybackSpeed(): Int {
        return when (musicPrefsUseCase.getPlaybackSpeed()) {
            .5f -> 0
            .8f -> 1
            1f -> 2
            1.2f -> 3
            1.5f -> 4
            2f -> 5
            3f -> 6
            else -> 2
        }
    }

    fun setPlaybackSpeed(itemId: Int) {
        val speed = when (itemId) {
            R.id.speed50 -> .5f
            R.id.speed80 -> .8f
            R.id.speed100 -> 1f
            R.id.speed120 -> 1.2f
            R.id.speed150 -> 1.5f
            R.id.speed200 -> 2f
            R.id.speed300 -> 3f
            else -> 1f
        }
        musicPrefsUseCase.setPlaybackSpeed(speed)
    }

    private fun MediaSessionCompat.QueueItem.toDisplayableItem(): DisplayableItem {
        val description = this.description

        return DisplayableItem(
            R.layout.item_mini_queue,
            MediaId.fromString(description.mediaId!!),
            description.title!!.toString(),
            description.subtitle!!.toString(),
            isPlayable = true,
            trackNumber = "${this.queueId}"
        )
    }

}