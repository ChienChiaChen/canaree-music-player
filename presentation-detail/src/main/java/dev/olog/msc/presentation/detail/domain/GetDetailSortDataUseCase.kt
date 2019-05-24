package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.presentation.detail.sort.DetailSort
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
    private val prefsGateway: SortPreferencesGateway

) {

    fun execute(param: MediaId): DetailSort {
        val arranging = prefsGateway.getDetailSortArranging()
        val sortType = getSortType(param)
        return DetailSort(sortType, arranging)
    }

    private fun getSortType(mediaId: MediaId): SortType {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> prefsGateway.getDetailFolderSortOrder()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> prefsGateway.getDetailPlaylistSortOrder()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> prefsGateway.getDetailAlbumSortOrder()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> prefsGateway.getDetailArtistSortOrder()
            MediaIdCategory.GENRES -> prefsGateway.getDetailGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}