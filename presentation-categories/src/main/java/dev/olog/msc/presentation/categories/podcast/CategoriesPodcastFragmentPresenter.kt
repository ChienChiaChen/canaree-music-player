package dev.olog.msc.presentation.categories.podcast

import androidx.core.math.MathUtils.clamp
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class CategoriesPodcastFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesGateway
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerPodcastLastPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerPodcastLastPage(page)
    }

    fun getCategories() = appPrefsUseCase
            .getPodcastLibraryCategories()
            .filter { it.visible }

}