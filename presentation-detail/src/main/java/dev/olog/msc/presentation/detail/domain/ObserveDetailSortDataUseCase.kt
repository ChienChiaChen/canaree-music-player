package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableFlowWithParam
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.shared.core.channel.combineLatest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortDataUseCase @Inject constructor(
    scheduler: IoDispatcher,
    private val prefsGateway: SortPreferencesGateway

) : ObservableFlowWithParam<DetailSort, MediaId>(scheduler) {

    override suspend fun buildUseCaseObservable(param: MediaId): Flow<DetailSort> {
        return combineLatest(
            prefsGateway.observeSortArranging(),
            getSortType(param)
        ) { arranging, sortType -> DetailSort(sortType, arranging) }
            .distinctUntilChanged()
    }

    private fun getSortType(mediaId: MediaId): Flow<SortType> {
        val category = mediaId.category
        return when (category) {
            MediaIdCategory.FOLDERS -> prefsGateway.observeFolderSortOrder()
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> prefsGateway.observePlaylistSortOrder()
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> prefsGateway.observeAlbumSortOrder()
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> prefsGateway.observeArtistSortOrder()
            MediaIdCategory.GENRES -> prefsGateway.observeGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}