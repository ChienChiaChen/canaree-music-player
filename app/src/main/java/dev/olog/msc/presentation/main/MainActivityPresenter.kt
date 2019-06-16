package dev.olog.msc.presentation.main

import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) {

    fun isFirstAccess() : Boolean {
        return appPreferencesUseCase.isFirstAccess()
    }

    fun getLastBottomViewPage(): BottomNavigationPage = appPreferencesUseCase.getLastBottomViewPage()

    fun setLastBottomViewPage(page: BottomNavigationPage){
        appPreferencesUseCase.setLastBottomViewPage(page)
    }

    fun canShowPodcastCategory(): Boolean {
        return appPreferencesUseCase.canShowPodcastCategory()
    }

}