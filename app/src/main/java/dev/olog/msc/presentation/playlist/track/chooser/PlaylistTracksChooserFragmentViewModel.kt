package dev.olog.msc.presentation.playlist.track.chooser

import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.lifecycle.*
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.mapToList
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.interactor.InsertCustomTrackListRequest
import dev.olog.msc.core.interactor.InsertCustomTrackListToPlaylist
import dev.olog.msc.core.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
import dev.olog.msc.presentation.base.extensions.asLiveData
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.playlist.track.chooser.model.PlaylistTrack
import dev.olog.msc.presentation.playlist.track.chooser.model.toDisplayableItem
import dev.olog.msc.presentation.playlist.track.chooser.model.toPlaylistTrack
import dev.olog.msc.shared.extensions.mapToList
import dev.olog.msc.shared.extensions.toList
import dev.olog.msc.shared.extensions.toggle
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class PlaylistTracksChooserFragmentViewModel @Inject constructor(
        private val playlistType: PlaylistType,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllPodcastsUseCase: GetAllPodcastUseCase,
        private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private val showOnlyFiltered = MutableLiveData<Boolean>()

    init {
        showOnlyFiltered.postValue(false)
    }

    fun getAllSongs(filter: Observable<String>): LiveData<List<DisplayableItem>> {
        return Transformations.switchMap(showOnlyFiltered) { onlyFiltered ->
            if (onlyFiltered){
                getPlaylistTypeTracks()
                        .map { songs -> songs.filter { selectedIds.contains(it.id) } }
            } else {
                Observables.combineLatest(
                        filter, getPlaylistTypeTracks()
                ) { query, tracks -> tracks.filter {
                        it.title.contains(query, true)  ||
                        it.artist.contains(query, true) ||
                        it.album.contains(query, true)
                } }
            }.mapToList { it.toDisplayableItem() }
                    .asLiveData()
        }
    }

    private fun getPlaylistTypeTracks(): Observable<List<PlaylistTrack>> = when (playlistType) {
        PlaylistType.PODCAST -> runBlocking { getAllPodcastsUseCase.execute() }.mapToList { it.toPlaylistTrack() }
        PlaylistType.TRACK -> runBlocking { getAllSongsUseCase.execute() }.mapToList { it.toPlaylistTrack() }
        PlaylistType.AUTO -> throw IllegalArgumentException("type auto not valid")
    }.map { list -> list.sortedBy { it.title.toLowerCase() } }
        .asObservable()

    fun toggleItem(mediaId: MediaId){
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountLiveData.postValue(selectedIds.size())
    }

    fun toggleShowOnlyFiltered(){
        val onlyFiltered = showOnlyFiltered.value!!
        showOnlyFiltered.postValue(!onlyFiltered)

    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

    fun savePlaylist(playlistTitle: String) = viewModelScope.launch{
        if (selectedIds.isEmpty()){
            return@launch
        }
        insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(
                playlistTitle, selectedIds.toList(), playlistType
        ))
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}