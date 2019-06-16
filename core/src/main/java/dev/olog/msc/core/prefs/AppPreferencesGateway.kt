package dev.olog.msc.core.prefs

import dev.olog.msc.core.entity.*
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File

interface AppPreferencesGateway : Sorting {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun isFirstAccess(): Boolean

    fun getVisibleTabs(): Observable<BooleanArray>

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
    fun observeLastFmCredentials(): Observable<UserCredentials>
    fun setLastFmCredentials(user: UserCredentials)

    fun getSyncAdjustment(): Long
    fun setSyncAdjustment(value: Long)

    fun observeDefaultMusicFolder(): Observable<File>
    fun getDefaultMusicFolder(): File
    fun setDefaultMusicFolder(file: File)

    fun observeLibraryNewVisibility(): Observable<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Observable<Boolean>

    fun canShowPodcastCategory(): Boolean
    fun isAdaptiveColorEnabled(): Boolean

}

interface Sorting {

    fun getAllTracksSortOrder(): LibrarySortType
    fun getAllAlbumsSortOrder(): LibrarySortType
    fun getAllArtistsSortOrder(): LibrarySortType

    fun observeAllTracksSortOrder(): Observable<LibrarySortType>
    fun observeAllAlbumsSortOrder(): Observable<LibrarySortType>
    fun observeAllArtistsSortOrder(): Observable<LibrarySortType>

    fun setAllTracksSortOrder(sortType: LibrarySortType)
    fun setAllAlbumsSortOrder(sortType: LibrarySortType)
    fun setAllArtistsSortOrder(sortType: LibrarySortType)

    fun getFolderSortOrder() : Observable<SortType>
    fun getPlaylistSortOrder() : Observable<SortType>
    fun getAlbumSortOrder() : Observable<SortType>
    fun getArtistSortOrder() : Observable<SortType>
    fun getGenreSortOrder() : Observable<SortType>

    fun setFolderSortOrder(sortType: SortType) : Completable
    fun setPlaylistSortOrder(sortType: SortType) : Completable
    fun setAlbumSortOrder(sortType: SortType) : Completable
    fun setArtistSortOrder(sortType: SortType) : Completable
    fun setGenreSortOrder(sortType: SortType) : Completable

    fun getSortArranging(): Observable<SortArranging>
    fun toggleSortArranging(): Completable
}