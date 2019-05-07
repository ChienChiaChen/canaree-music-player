package dev.olog.msc.presentation.detail.domain.sort

import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import io.reactivex.Observable
import javax.inject.Inject

class GetDetailTabsVisibilityUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun execute(): Observable<BooleanArray> {
        return gateway.getVisibleTabs()
    }

}