package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.detail.sort.DetailSort
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
    private val prefsGateway: AppPreferencesGateway

) {

    fun execute(param: MediaId): DetailSort {
        val arranging = prefsGateway.getSortArranging()
        val sortType = getSortType(param)
        return DetailSort(sortType, arranging)
    }

    private fun getSortType(mediaId: MediaId): SortType {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> prefsGateway.getFolderSortOrder()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> prefsGateway.getPlaylistSortOrder()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> prefsGateway.getAlbumSortOrder()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> prefsGateway.getArtistSortOrder()
            MediaIdCategory.GENRES -> prefsGateway.getGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}