package dev.olog.msc.presentation.offlinelyrics

import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.offlinelyrics.BaseOfflineLyricsPresenter
import dev.olog.msc.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.msc.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import dev.olog.msc.shared.TrackUtils
import io.reactivex.Completable
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
        observeUseCase: ObserveOfflineLyricsUseCase,
        insertUseCase: InsertOfflineLyricsUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
        appPreferencesUseCase: AppPreferencesGateway

) : BaseOfflineLyricsPresenter(appPreferencesUseCase, observeUseCase, insertUseCase) {

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentMetadata(title: String, artist: String){
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != TrackUtils.UNKNOWN_ARTIST){
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun showAddLyricsIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

}