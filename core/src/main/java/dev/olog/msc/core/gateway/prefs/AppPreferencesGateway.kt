package dev.olog.msc.core.gateway.prefs

import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AppPreferencesGateway {

    fun getLastBottomViewPage(): Int
    fun setLastBottomViewPage(page: Int)

    fun isFirstAccess(): Boolean

    fun getVisibleTabs(): BooleanArray

    fun getViewPagerLibraryLastPage(): Int
    fun setViewPagerLibraryLastPage(lastPage: Int)

    fun getViewPagerPodcastLastPage(): Int
    fun setViewPagerPodcastLastPage(lastPage: Int)

    fun getLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior>
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>)

    fun getPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>)

    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun resetSleepTimer()
    fun setSleepTimer(sleepFrom: Long, sleepTime: Long)
    fun getSleepTime() : Long
    fun getSleepFrom() : Long

    fun observePlayerControlsVisibility(): Observable<Boolean>

    fun setDefault(): Completable

    fun canAutoCreateImages(): Boolean

    fun getLastFmCredentials(): UserCredentials
    suspend fun observeLastFmCredentials(): Flow<UserCredentials>
    fun setLastFmCredentials(user: UserCredentials)

    fun getSyncAdjustment(): Long
    fun setSyncAdjustment(value: Long)

    fun observeDefaultMusicFolder(): Observable<File>
    fun getDefaultMusicFolder(): File
    fun setDefaultMusicFolder(file: File)

    fun canShowLibraryNewVisibility(): Boolean
    fun canShowLibraryRecentPlayedVisibility(): Boolean

    fun canShowPodcastCategory(): Boolean
    fun isAdaptiveColorEnabled(): Boolean

    fun observeLockscreenArtworkEnabled(): Observable<Boolean>

    fun getShowFolderAsTreeView(): Boolean

}

interface SortPreferencesGateway {

    fun getAllTracksSortOrder(): LibrarySortType
    fun getAllAlbumsSortOrder(): LibrarySortType
    fun getAllArtistsSortOrder(): LibrarySortType

    fun observeAllTracksSortOrder(): Flow<LibrarySortType>
    fun observeAllAlbumsSortOrder(): Flow<LibrarySortType>
    fun observeAllArtistsSortOrder(): Flow<LibrarySortType>

    suspend fun setAllTracksSortOrder(sortType: LibrarySortType)
    suspend fun setAllAlbumsSortOrder(sortType: LibrarySortType)
    suspend fun setAllArtistsSortOrder(sortType: LibrarySortType)

    fun observeFolderSortOrder() : Flow<SortType>
    fun observePlaylistSortOrder() : Flow<SortType>
    fun observeAlbumSortOrder() : Flow<SortType>
    fun observeArtistSortOrder() : Flow<SortType>
    fun observeGenreSortOrder() : Flow<SortType>
    fun getFolderSortOrder() : SortType
    fun getPlaylistSortOrder() : SortType
    fun getAlbumSortOrder() : SortType
    fun getArtistSortOrder() : SortType
    fun getGenreSortOrder() : SortType

    suspend fun setFolderSortOrder(sortType: SortType)
    suspend fun setPlaylistSortOrder(sortType: SortType)
    suspend fun setAlbumSortOrder(sortType: SortType)
    suspend fun setArtistSortOrder(sortType: SortType)
    suspend fun setGenreSortOrder(sortType: SortType)

    fun observeSortArranging(): Flow<SortArranging>
    fun getSortArranging(): SortArranging
    suspend fun toggleSortArranging()
}