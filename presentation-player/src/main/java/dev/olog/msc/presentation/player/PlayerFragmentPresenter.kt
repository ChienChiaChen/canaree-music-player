package dev.olog.msc.presentation.player

import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.pro.IBilling
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

class PlayerFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesGateway
) {

    fun observePlayerControlsVisibility(billing: IBilling): Observable<Boolean> {
        return Observables.combineLatest(
                billing.observeIsPremium(),
                appPrefsUseCase.observePlayerControlsVisibility()
        ) { premium, show -> premium && show }
    }

}