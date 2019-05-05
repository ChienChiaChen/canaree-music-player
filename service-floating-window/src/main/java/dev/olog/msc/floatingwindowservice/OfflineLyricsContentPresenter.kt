package dev.olog.msc.floatingwindowservice

import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.offlinelyrics.BaseOfflineLyricsPresenter
import dev.olog.msc.offlinelyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.msc.offlinelyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsContentPresenter @Inject constructor(
        appPreferencesUseCase: AppPreferencesGateway,
        observeUseCase: ObserveOfflineLyricsUseCase,
        insertUseCase: InsertOfflineLyricsUseCase

) : BaseOfflineLyricsPresenter(appPreferencesUseCase, observeUseCase, insertUseCase)