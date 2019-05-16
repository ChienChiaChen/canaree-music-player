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

interface AppPreferencesGateway : Sorting {

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

    fun observeAutoCreateImages(): Observable<Boolean>

    fun getLastFmCredentials(): UserCredentials
    fun observeLastFmCredentials(): Observable<UserCredentials>
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

    fun ignoreMediaStoreCover(): Boolean

}

interface Sorting {

    fun getAllTracksSortOrder(): LibrarySortType
    fun getAllAlbumsSortOrder(): LibrarySortType
    fun getAllArtistsSortOrder(): LibrarySortType

    fun observeAllTracksSortOrder(): Flow<LibrarySortType>
    fun observeAllAlbumsSortOrder(): Flow<LibrarySortType>
    fun observeAllArtistsSortOrder(): Flow<LibrarySortType>

    suspend fun setAllTracksSortOrder(sortType: LibrarySortType)
    suspend fun setAllAlbumsSortOrder(sortType: LibrarySortType)
    suspend fun setAllArtistsSortOrder(sortType: LibrarySortType)

    fun getFolderSortOrder() : Flow<SortType>
    fun getPlaylistSortOrder() : Flow<SortType>
    fun getAlbumSortOrder() : Flow<SortType>
    fun getArtistSortOrder() : Flow<SortType>
    fun getGenreSortOrder() : Flow<SortType>

    suspend fun setFolderSortOrder(sortType: SortType)
    suspend fun setPlaylistSortOrder(sortType: SortType)
    suspend fun setAlbumSortOrder(sortType: SortType)
    suspend fun setArtistSortOrder(sortType: SortType)
    suspend fun setGenreSortOrder(sortType: SortType)

    fun getSortArranging(): Flow<SortArranging>
    suspend fun toggleSortArranging()
}