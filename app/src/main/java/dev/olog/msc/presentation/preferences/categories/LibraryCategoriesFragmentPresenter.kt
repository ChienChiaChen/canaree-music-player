package dev.olog.msc.presentation.preferences.categories

import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.core.prefs.AppPreferencesGateway
import dev.olog.msc.core.MediaIdCategory
import javax.inject.Inject

class LibraryCategoriesFragmentPresenter @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesGateway
){

    fun getDefaultDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior>{
        if (category == MediaIdCategory.PODCASTS){
            return appPreferencesUseCase.getDefaultPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getDefaultLibraryCategories()
    }

    fun getDataSet(category: MediaIdCategory) : List<LibraryCategoryBehavior> {
        if (category == MediaIdCategory.PODCASTS){
            return appPreferencesUseCase.getPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getLibraryCategories()
    }

    fun setDataSet(category: MediaIdCategory, list: List<LibraryCategoryBehavior>){
        if (category == MediaIdCategory.PODCASTS){
            appPreferencesUseCase.setPodcastLibraryCategories(list)
        } else {
            appPreferencesUseCase.setLibraryCategories(list)
        }

    }

}