package dev.olog.msc.presentation.home

import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.home.domain.IsRepositoryEmptyUseCase
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun isFirstAccess(): Boolean {
        return appPreferencesUseCase.isFirstAccess()
    }

    fun getLastBottomViewPage(): Int = appPreferencesUseCase.getLastBottomViewPage()

    fun setLastBottomViewPage(page: Int) {
        appPreferencesUseCase.setLastBottomViewPage(page)
    }

    fun canShowPodcastCategory(): Boolean {
        return appPreferencesUseCase.canShowPodcastCategory()
    }

}