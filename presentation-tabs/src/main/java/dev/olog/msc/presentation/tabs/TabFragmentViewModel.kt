package dev.olog.msc.presentation.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.base.extensions.asLiveData
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.data.TabFragmentDataProvider
import javax.inject.Inject

class TabFragmentViewModel @Inject constructor(
        private val dataProvider: TabFragmentDataProvider,
        private val appPreferencesUseCase: AppPreferencesGateway

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: MediaIdCategory): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList[category]
        if (liveData == null) {
            liveData = dataProvider.getData(category).asLiveData()
            liveDataList[category] = liveData
        }

        return liveData
    }

    fun getAllTracksSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllAlbumsSortOrder()
    }

    fun getAllArtistsSortOrder(): LibrarySortType {
        return appPreferencesUseCase.getAllArtistsSortOrder()
    }

}