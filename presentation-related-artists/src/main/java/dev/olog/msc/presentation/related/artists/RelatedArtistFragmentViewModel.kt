package dev.olog.msc.presentation.related.artists

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.interactor.GetItemTitleUseCase
import dev.olog.msc.core.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.shared.utils.TextUtils
import javax.inject.Inject

class RelatedArtistFragmentViewModel @Inject constructor(
        resources: Resources,
        mediaId: MediaId,
        relatedArtistsUseCase: GetRelatedArtistsUseCase,
        getItemTitleUseCase: GetItemTitleUseCase

): ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

//    val data: LiveData<List<DisplayableItem>> = useCase.execute(mediaId)
//            .mapToList { it.toRelatedArtist(resources) }
//            .map { it.sortedWith(Comparator { o1, o2 -> collator.safeCompare(o1.title, o2.title) }) }
//            .asLiveData()
    val data: LiveData<List<DisplayableItem>> = TODO()

//    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()
    val itemTitle: LiveData<String> = TODO()

    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
        val albums = if (this.albums == 0) "" else {
            "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
        }

        return DisplayableItem(
                R.layout.item_related_artist,
                MediaId.artistId(id),
                this.name,
                "$albums$songs",
                this.image
        )
    }

}