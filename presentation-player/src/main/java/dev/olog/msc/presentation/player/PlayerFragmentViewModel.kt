package dev.olog.msc.presentation.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.favorite.FavoriteEnum
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.theme.player.theme.*
import dev.olog.msc.shared.ui.imageview.adaptive.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val processorPublisher = BehaviorSubject.create<ProcessorColors>()
    private val palettePublisher = BehaviorSubject.create<PaletteColors>()

    fun observeProcessorColors(): Observable<ProcessorColors> = processorPublisher
        .map {
            if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                it
            } else {
                InvalidProcessColors
            }
        }
        .filter { it is ValidProcessorColors }
        .observeOn(AndroidSchedulers.mainThread())

    fun observePaletteColors(): Observable<PaletteColors> = palettePublisher
        .map {
            if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                it
            } else {
                InvalidPaletteColors
            }
        }
        .filter { it is ValidPaletteColors }
        .observeOn(AndroidSchedulers.mainThread())

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.onNext(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.onNext(palette)
    }

    private val miniQueue = MutableLiveData<List<DisplayableItem>>()

    private val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    fun getCurrentTrackId() = currentTrackIdPublisher.value!!

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.onNext(trackId)
    }

    fun observeMiniQueue(): LiveData<List<DisplayableItem>> = miniQueue

    fun updateQueue(list: List<DisplayableItem>) {
        miniQueue.postValue(list)
    }

    private val progressPublisher = BehaviorSubject.createDefault(0)

    val observeProgress: Observable<Int> = progressPublisher

    fun updateProgress(progress: Int) {
        progressPublisher.onNext(progress)
    }

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

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

    val onFavoriteStateChanged: Observable<FavoriteEnum> = observeFavoriteAnimationUseCase.execute()

    val skipToNextVisibility = musicPrefsUseCase
        .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()

    fun showLyricsTutorialIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

    fun getPlaybackSpeed(): Int {
        val speed = musicPrefsUseCase.getPlaybackSpeed()
        return when (speed) {
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


}