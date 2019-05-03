package dev.olog.msc.presentation.library.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.Lazy
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Observable
import javax.inject.Inject

class TabFragmentViewModel @Inject constructor(
        private val data: Lazy<Map<MediaIdCategory, Observable<List<DisplayableItem>>>>,
        private val appPreferencesUseCase: AppPreferencesGateway

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<List<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: MediaIdCategory): LiveData<List<DisplayableItem>> {
        var liveData: LiveData<List<DisplayableItem>>? = liveDataList[category]
        if (liveData == null) {
            liveData = data.get()[category]!!.asLiveData()
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

//    fun observeAlbumSpanSize(category: MediaIdCategory): Observable<GridSpanSize> {
//        return appPreferencesUseCase.observeSpanSize(category)
//    }

}