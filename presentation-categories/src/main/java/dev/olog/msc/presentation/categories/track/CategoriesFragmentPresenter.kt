package dev.olog.msc.presentation.categories.track

import androidx.core.math.MathUtils.clamp
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import javax.inject.Inject

class CategoriesFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesGateway,
        private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerLibraryLastPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerLibraryLastPage(page)
    }

    fun canShowFloatingWindowTutorial(): Boolean{
        return tutorialPreferenceUseCase.canShowFloatingWindowTutorial()
    }

    fun getCategories() = appPrefsUseCase
            .getLibraryCategories()
            .filter { it.visible }

}