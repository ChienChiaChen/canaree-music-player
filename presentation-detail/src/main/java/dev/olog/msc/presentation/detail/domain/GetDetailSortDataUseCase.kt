package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.coroutines.IoDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.coroutines.combineLatest
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.detail.sort.DetailSort
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDetailSortDataUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val prefsGateway: AppPreferencesGateway

) : ObservableFlowWithParam<DetailSort, MediaId>(scheduler) {

    override suspend fun buildUseCaseObservable(param: MediaId): Flow<DetailSort> {
        return combineLatest(
            prefsGateway.getSortArranging(),
            getSortType(param)
        ) { arranging, sortType -> DetailSort(sortType, arranging) }
    }

    private fun getSortType(mediaId: MediaId): Flow<SortType> {
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