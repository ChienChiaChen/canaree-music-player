package dev.olog.msc.presentation.playlist.chooser

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Playlist
import dev.olog.msc.core.interactor.all.ObserveAllPlaylistsUseCase
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.utils.handleSongListSize
import dev.olog.msc.shared.core.flow.mapToList
import dev.olog.msc.shared.ui.extensions.liveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistChooserActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllPlaylistsUseCase: ObserveAllPlaylistsUseCase
) : ViewModel() {

    private val data = liveDataOf<List<DisplayableItem>>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            getAllPlaylistsUseCase.execute()
                .mapToList { it.toDisplayableItem(context.resources) }
                .collect { data.value = it }
        }
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data

    private fun Playlist.toDisplayableItem(resources: Resources): DisplayableItem {
        val size = handleSongListSize(resources, size)

        return DisplayableItem(
            R.layout.item_playlist_chooser,
            MediaId.playlistId(id),
            title,
            size
        )
    }

}