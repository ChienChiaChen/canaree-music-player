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

    fun setAllTracksSortOrder(sortType: LibrarySortType)
    fun setAllAlbumsSortOrder(sortType: LibrarySortType)
    fun setAllArtistsSortOrder(sortType: LibrarySortType)

    fun getFolderSortOrder() : SortType
    fun getPlaylistSortOrder() : SortType
    fun getAlbumSortOrder() : SortType
    fun getArtistSortOrder() : SortType
    fun getGenreSortOrder() : SortType

    fun setFolderSortOrder(sortType: SortType) : Completable
    fun setPlaylistSortOrder(sortType: SortType) : Completable
    fun setAlbumSortOrder(sortType: SortType) : Completable
    fun setArtistSortOrder(sortType: SortType) : Completable
    fun setGenreSortOrder(sortType: SortType) : Completable

    fun getSortArranging(): SortArranging
    fun toggleSortArranging(): Completable
}