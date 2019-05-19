package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.mapToList
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.interactor.all.ObserveAllPlaylistsUseCase
import dev.olog.msc.presentation.base.extensions.liveDataOf
import dev.olog.msc.presentation.base.model.DisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistChooserActivityViewModel @Inject constructor(
    resources: Resources,
    private val getAllPlaylistsUseCase: ObserveAllPlaylistsUseCase
) : ViewModel() {

    private val data = liveDataOf<List<DisplayableItem>>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            getAllPlaylistsUseCase.execute()
                .mapToList { it.toDisplayableItem(resources) }
                .collect { data.value = it }
        }
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        val size = DisplayableItem.handleSongListSize(resources, size)

        return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title,
            size
        )
    }

}