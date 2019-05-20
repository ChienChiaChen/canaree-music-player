package dev.olog.msc.presentation.create.playlist

import android.util.LongSparseArray
import androidx.core.util.isEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.interactor.InsertCustomTrackListRequest
import dev.olog.msc.core.interactor.InsertCustomTrackListToPlaylist
import dev.olog.msc.presentation.base.OnFail
import dev.olog.msc.presentation.base.OnSuccess
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.shared.extensions.toList
import dev.olog.msc.shared.extensions.toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreatePlaylistViewModel @Inject constructor(
    private val dataSource: CreatePlaylistDataSourceFactory,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private var showOnlyFiltered: Boolean = false

    private lateinit var playlistType: PlaylistType

    private var data: LiveData<PagedList<DisplayableItem>>? = null

    fun observeData(playlistType: PlaylistType): LiveData<PagedList<DisplayableItem>>{
        this.playlistType = playlistType

        if (data == null){
            val config = PagedList.Config.Builder()
                .setPageSize(30)
                .setEnablePlaceholders(true)
                .build()

            dataSource.playlistType = playlistType
            data = LivePagedListBuilder(dataSource, config).build()
        }
        return data!!
    }

    fun updateFilter(filterBy: String) {
        dataSource.updateFilterBy(filterBy)
    }

    fun toggleItem(mediaId: MediaId) {
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountLiveData.postValue(selectedIds.size())
    }

    // returns false if can't swap to selectedOnly
    fun toggleShowOnlyFiltered(): Boolean {
        if (!showOnlyFiltered && selectedIds.isEmpty()){
            return false
        }
        showOnlyFiltered = !showOnlyFiltered
        if (showOnlyFiltered) {
            dataSource.updateSelectedIds(selectedIds.toList())
        } else {
            dataSource.updateSelectedIds(null)
        }
        return true
    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

    fun savePlaylist(playlistType: PlaylistType, playlistTitle: String, onSuccess: OnSuccess, onFail: OnFail) = viewModelScope.launch(Dispatchers.Default) {
        if (selectedIds.isEmpty()) {
            withContext(Dispatchers.Main) { onFail() }
            return@launch
        }
        insertCustomTrackListToPlaylist.execute(
            InsertCustomTrackListRequest(
                playlistTitle,
                selectedIds.toList(),
                playlistType
            )
        )
        withContext(Dispatchers.Main){ onSuccess() }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        dataSource.onDetach()
    }

}