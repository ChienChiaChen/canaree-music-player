package dev.olog.msc.presentation.related.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.interactor.ObserveItemTitleUseCase
import dev.olog.msc.presentation.base.extensions.liveDataOf
import dev.olog.msc.presentation.base.model.DisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class RelatedArtistFragmentViewModel @Inject constructor(
    private val mediaId: MediaId,
    observeItemTitleUseCase: ObserveItemTitleUseCase,
    private val relatedArtistsDataSource: RelatedArtistsDataSourceFactory

) : ViewModel() {

    val data: LiveData<PagedList<DisplayableItem>>
    private val titleLiveData = liveDataOf<String>()
    val itemOrdinal = mediaId.category.ordinal

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(true)
            .build()
        data = LivePagedListBuilder(relatedArtistsDataSource, config).build()

        viewModelScope.launch(Dispatchers.Default) {
            observeItemTitleUseCase.execute(mediaId)
                .collect { titleLiveData.postValue(it) }
        }
    }

    fun observeTitle(): LiveData<String> {
        return titleLiveData
    }

    override fun onCleared() {
        viewModelScope.cancel()
        relatedArtistsDataSource.onDetach()
    }

}