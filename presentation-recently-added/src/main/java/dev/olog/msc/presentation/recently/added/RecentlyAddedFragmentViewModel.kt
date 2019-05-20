package dev.olog.msc.presentation.recently.added

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.interactor.ObserveItemTitleUseCase
import dev.olog.msc.presentation.base.extensions.liveDataOf
import dev.olog.msc.presentation.base.model.DisplayableItem
import kotlinx.coroutines.cancel
import javax.inject.Inject

internal class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: MediaId,
    observeItemTitleUseCase: ObserveItemTitleUseCase,
    private val recentlyAddedDataSource: RecentlyAddedDataSourceFactory

) : ViewModel() {

    val data: LiveData<PagedList<DisplayableItem>>
    private val titleLiveData = liveDataOf<String>()
    val itemOrdinal = mediaId.category.ordinal

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(true)
            .build()
        data = LivePagedListBuilder(recentlyAddedDataSource, config).build()
    }

    fun observeTitle(): LiveData<String> {
        return titleLiveData
    }

    override fun onCleared() {
        viewModelScope.cancel()
        recentlyAddedDataSource.onDetach()
    }

}
