package dev.olog.msc.presentation.recently.added

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetItemTitleUseCase
import dev.olog.msc.core.interactor.added.GetRecentlyAddedUseCase
import dev.olog.msc.presentation.base.extensions.asLiveData
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.extensions.mapToList
import javax.inject.Inject

class RecentlyAddedFragmentViewModel @Inject constructor(
        mediaId: MediaId,
        useCase: GetRecentlyAddedUseCase,
        getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data : LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
            .mapToList { it.toRecentDetailDisplayableItem(mediaId) }
            .asLiveData()

//    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()
    val itemTitle : LiveData<String> = TODO()

}

private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableItem(
            R.layout.item_recently_added,
            MediaId.playableItem(parentId, id),
            title,
            TrackUtils.adjustArtist(artist),
            image,
            true
    )
}


