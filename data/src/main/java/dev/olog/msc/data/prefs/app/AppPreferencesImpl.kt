package dev.olog.msc.data.prefs.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.core.entity.SearchFilters
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.data.R
import dev.olog.msc.data.utils.safeGetCanonicalPath
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.BackpressureStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import java.io.File
import javax.inject.Inject

internal class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences,
    private val rxPreferences: RxSharedPreferences

) : AppPreferencesGateway,
    SortPreferencesGateway by AppSortingImpl(preferences, rxPreferences) {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"

        private const val VIEW_PAGER_LAST_PAGE = "$TAG.VIEW_PAGER_LAST_PAGE"
        private const val VIEW_PAGER_PODCAST_LAST_PAGE = "$TAG.VIEW_PAGER_PODCAST_LAST_PAGE"
        private const val BOTTOM_VIEW_LAST_PAGE = "$TAG.BOTTOM_VIEW_2"

        private const val SLEEP_TIME = "$TAG.SLEEP_TIME"
        private const val SLEEP_FROM = "$TAG.FROM_WHEN"


        private const val CATEGORY_FOLDER_ORDER = "$TAG.CATEGORY_FOLDER_ORDER"
        private const val CATEGORY_PLAYLIST_ORDER = "$TAG.CATEGORY_PLAYLIST_ORDER"
        private const val CATEGORY_SONG_ORDER = "$TAG.CATEGORY_SONG_ORDER"
        private const val CATEGORY_ALBUM_ORDER = "$TAG.CATEGORY_ALBUM_ORDER"
        private const val CATEGORY_ARTIST_ORDER = "$TAG.CATEGORY_ARTIST_ORDER"
        private const val CATEGORY_GENRE_ORDER = "$TAG.CATEGORY_GENRE_ORDER"

        private const val CATEGORY_FOLDER_VISIBILITY = "$TAG.CATEGORY_FOLDER_VISIBILITY"
        private const val CATEGORY_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PLAYLIST_VISIBILITY"
        private const val CATEGORY_SONG_VISIBILITY = "$TAG.CATEGORY_SONG_VISIBILITY"
        private const val CATEGORY_ALBUM_VISIBILITY = "$TAG.CATEGORY_ALBUM_VISIBILITY"
        private const val CATEGORY_ARTIST_VISIBILITY = "$TAG.CATEGORY_ARTIST_VISIBILITY"
        private const val CATEGORY_GENRE_VISIBILITY = "$TAG.CATEGORY_GENRE_VISIBILITY"

        private const val CATEGORY_PODCAST_PLAYLIST_ORDER = "$TAG.CATEGORY_PODCAST_PLAYLIST_ORDER"
        private const val CATEGORY_PODCAST_ORDER = "$TAG.CATEGORY_PODCAST_ORDER"
        private const val CATEGORY_PODCAST_ALBUM_ORDER = "$TAG.CATEGORY_PODCAST_ALBUM_ORDER"
        private const val CATEGORY_PODCAST_ARTIST_ORDER = "$TAG.CATEGORY_PODCAST_ARTIST_ORDER"

        private const val CATEGORY_PODCAST_PLAYLIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_PODCAST_PLAYLIST_VISIBILITY"
        private const val CATEGORY_PODCAST_VISIBILITY = "$TAG.CATEGORY_PODCAST_VISIBILITY"
        private const val CATEGORY_PODCAST_ALBUM_VISIBILITY = "$TAG.CATEGORY_PODCAST_ALBUM_VISIBILITY"
        private const val CATEGORY_PODCAST_ARTIST_VISIBILITY = "$TAG.CATEGORY_PODCAST_ARTIST_VISIBILITY"

        private const val LAST_FM_USERNAME = "$TAG.LAST_FM_USERNAME"
        private const val LAST_FM_PASSWORD = "$TAG.LAST_FM_PASSWORD"

        private const val SYNC_ADJUSTMENT = "$TAG.SYNC_ADJUSTMENT"

        private const val BLACKLIST = "$TAG.BLACKLIST"

        private const val DEFAULT_MUSIC_FOLDER = "$TAG.DEFAULT_MUSIC_FOLDER"

        private const val SEARCH_FILTERS = "$TAG.SEARCH_FILTERS"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }

    override fun getViewPagerLibraryLastPage(): Int {
        return preferences.getInt(VIEW_PAGER_LAST_PAGE, 2)
    }

    override fun setViewPagerLibraryLastPage(lastPage: Int) {
        preferences.edit { putInt(VIEW_PAGER_LAST_PAGE, lastPage) }
    }

    override fun getViewPagerPodcastLastPage(): Int {
        return preferences.getInt(VIEW_PAGER_PODCAST_LAST_PAGE, 2)
    }

    override fun setViewPagerPodcastLastPage(lastPage: Int) {
        preferences.edit { putInt(VIEW_PAGER_PODCAST_LAST_PAGE, lastPage) }
    }

    override fun getLastBottomViewPage(): BottomNavigationPage {
        val result = preferences.getString(BOTTOM_VIEW_LAST_PAGE, BottomNavigationPage.SONGS.toString())
        return BottomNavigationPage.valueOf(result)
    }

    override fun setLastBottomViewPage(page: BottomNavigationPage) {
        preferences.edit { putString(BOTTOM_VIEW_LAST_PAGE, page.toString()) }
    }

    override fun getVisibleTabs(): BooleanArray {
        val visibleSections = preferences.getStringSet(context.getString(R.string.prefs_detail_sections_key), setOf())!!
        val prefsDefault = listOf(
            R.string.prefs_detail_section_entry_value_most_played,
            R.string.prefs_detail_section_entry_value_recently_added,
            R.string.prefs_detail_section_entry_value_related_artists
        )
        return booleanArrayOf(
            visibleSections.contains(context.getString(prefsDefault[0])), // most played
            visibleSections.contains(context.getString(prefsDefault[1])), // recently added
            visibleSections.contains(context.getString(prefsDefault[2])) // related artists
        )
    }

    override fun getLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            LibraryCategoryBehavior(
                MediaIdCategory.FOLDERS,
                preferences.getBoolean(CATEGORY_FOLDER_VISIBILITY, true),
                preferences.getInt(CATEGORY_FOLDER_ORDER, 0)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PLAYLISTS,
                preferences.getBoolean(CATEGORY_PLAYLIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PLAYLIST_ORDER, 1)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.SONGS,
                preferences.getBoolean(CATEGORY_SONG_VISIBILITY, true),
                preferences.getInt(CATEGORY_SONG_ORDER, 2)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.ALBUMS,
                preferences.getBoolean(CATEGORY_ALBUM_VISIBILITY, true),
                preferences.getInt(CATEGORY_ALBUM_ORDER, 3)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.ARTISTS,
                preferences.getBoolean(CATEGORY_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_ARTIST_ORDER, 4)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.GENRES,
                preferences.getBoolean(CATEGORY_GENRE_VISIBILITY, true),
                preferences.getInt(CATEGORY_GENRE_ORDER, 5)
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        return MediaIdCategory.values()
            .take(6)
            .mapIndexed { index, category -> LibraryCategoryBehavior(category, true, index) }
    }

    override fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {
            val folder = behavior.first { it.category == MediaIdCategory.FOLDERS }
            putInt(CATEGORY_FOLDER_ORDER, folder.order)
            putBoolean(CATEGORY_FOLDER_VISIBILITY, folder.visible)

            val playlist = behavior.first { it.category == MediaIdCategory.PLAYLISTS }
            putInt(CATEGORY_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaIdCategory.SONGS }
            putInt(CATEGORY_SONG_ORDER, song.order)
            putBoolean(CATEGORY_SONG_VISIBILITY, song.visible)

            val album = behavior.first { it.category == MediaIdCategory.ALBUMS }
            putInt(CATEGORY_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaIdCategory.ARTISTS }
            putInt(CATEGORY_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_ARTIST_VISIBILITY, artist.visible)

            val genre = behavior.first { it.category == MediaIdCategory.GENRES }
            putInt(CATEGORY_GENRE_ORDER, genre.order)
            putBoolean(CATEGORY_GENRE_VISIBILITY, genre.visible)
        }
    }

    override fun getPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return listOf(
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS_PLAYLIST,
                preferences.getBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_PLAYLIST_ORDER, 0)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS,
                preferences.getBoolean(CATEGORY_PODCAST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ORDER, 1)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS_ALBUMS,
                preferences.getBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ALBUM_ORDER, 2)
            ),
            LibraryCategoryBehavior(
                MediaIdCategory.PODCASTS_ARTISTS,
                preferences.getBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, true),
                preferences.getInt(CATEGORY_PODCAST_ARTIST_ORDER, 3)
            )
        ).sortedBy { it.order }
    }

    override fun getDefaultPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        return MediaIdCategory.values()
            .drop(6)
            .take(4)
            .mapIndexed { index, category -> LibraryCategoryBehavior(category, true, index) }
    }

    override fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        preferences.edit {

            val playlist = behavior.first { it.category == MediaIdCategory.PODCASTS_PLAYLIST }
            putInt(CATEGORY_PODCAST_PLAYLIST_ORDER, playlist.order)
            putBoolean(CATEGORY_PODCAST_PLAYLIST_VISIBILITY, playlist.visible)

            val song = behavior.first { it.category == MediaIdCategory.PODCASTS }
            putInt(CATEGORY_PODCAST_ORDER, song.order)
            putBoolean(CATEGORY_PODCAST_VISIBILITY, song.visible)

            val album = behavior.first { it.category == MediaIdCategory.PODCASTS_ALBUMS }
            putInt(CATEGORY_PODCAST_ALBUM_ORDER, album.order)
            putBoolean(CATEGORY_PODCAST_ALBUM_VISIBILITY, album.visible)

            val artist = behavior.first { it.category == MediaIdCategory.PODCASTS_ARTISTS }
            putInt(CATEGORY_PODCAST_ARTIST_ORDER, artist.order)
            putBoolean(CATEGORY_PODCAST_ARTIST_VISIBILITY, artist.visible)
        }
    }

    override fun getBlackList(): Set<String> {
        return preferences.getStringSet(BLACKLIST, setOf())!!
    }

    override fun setBlackList(set: Set<String>) {
        preferences.edit { putStringSet(BLACKLIST, set) }
    }

    override fun resetSleepTimer() {
        setSleepTimer(-1L, -1L)
    }

    override fun setSleepTimer(sleepFrom: Long, sleepTime: Long) {
        preferences.edit {
            putLong(SLEEP_FROM, sleepFrom)
            putLong(SLEEP_TIME, sleepTime)
        }
    }

    override fun getSleepTime(): Long {
        return preferences.getLong(SLEEP_TIME, -1)
    }

    override fun getSleepFrom(): Long {
        return preferences.getLong(SLEEP_FROM, -1)
    }

    override fun observePlayerControlsVisibility(): Flow<Boolean> {
        val key = context.getString(R.string.prefs_player_controls_visibility_key)
        return rxPreferences.getBoolean(key, false)
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
    }

    override fun setDefault(defaultAccentColor: Int) {
        assertBackgroundThread()
        setLibraryCategories(getDefaultLibraryCategories())
        setPodcastLibraryCategories(getDefaultPodcastLibraryCategories())
        setBlackList(setOf())
        hideQuickAction()
        setDefaultVisibleSections()
        hideClassicPlayerControls()
        setDefaultAutoDownloadImages()
        setDefaultTheme()
        setLastFmCredentials(UserCredentials("", ""))
        setDefaultFolderView()
        setDefaultMusicFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
        setDefaultAccentColor(defaultAccentColor)
        setDefaultLibraryAlbumArtistVisibility()
        setDefaultPodcastVisibility()
        setDefaultAdaptiveColors()
        setDefaultLockscreenArtwork()
    }

    private fun setDefaultLockscreenArtwork() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_lockscreen_artwork_key), false)
        }
    }

    private fun setDefaultAdaptiveColors() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
        }
    }

    private fun setDefaultLibraryAlbumArtistVisibility() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_show_new_albums_artists_key), true)
            putBoolean(context.getString(R.string.prefs_show_recent_albums_artists_key), true)
        }
    }

    private fun setDefaultPodcastVisibility() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
        }
    }

    private fun setDefaultAccentColor(default: Int) {
        preferences.edit {
            putInt(context.getString(R.string.prefs_color_accent_key), default)
        }
    }

    override fun canAutoCreateImages(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
    }

    private fun setDefaultFolderView() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
        }
    }

    private fun setDefaultAutoDownloadImages() {
        preferences.edit {
            putString(
                context.getString(R.string.prefs_auto_download_images_key),
                context.getString(R.string.prefs_auto_download_images_entry_value_wifi)
            )
            putBoolean(context.getString(R.string.prefs_auto_create_images_key), true)
        }
    }

    private fun hideQuickAction() {
        preferences.edit {
            putString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        }
    }

    private fun setDefaultVisibleSections() {
        preferences.edit {
            val default = context.resources.getStringArray(R.array.prefs_detail_sections_entry_values_default).toSet()
            putStringSet(context.getString(R.string.prefs_detail_sections_key), default)
        }
    }

    private fun hideClassicPlayerControls() {
        preferences.edit {
            putBoolean(context.getString(R.string.prefs_player_controls_visibility_key), false)
        }
    }

    private fun setDefaultTheme() {
        preferences.edit {
            putString(
                context.getString(R.string.prefs_appearance_key),
                context.getString(R.string.prefs_appearance_entry_value_default)
            )
            putString(context.getString(R.string.prefs_dark_mode_2_key), context.getString(R.string.prefs_dark_mode_2_value_follow_system))
        }
    }

    /*
            Must be encrypted
         */
    override fun getLastFmCredentials(): UserCredentials {
        return UserCredentials(
            preferences.getString(LAST_FM_USERNAME, "")!!,
            preferences.getString(LAST_FM_PASSWORD, "")!!
        )
    }

    /*
        Must be encrypted
     */
    override suspend fun observeLastFmCredentials(): Flow<UserCredentials> {
        return rxPreferences.getString(LAST_FM_USERNAME, "")
            .asObservable()
            .map {
                UserCredentials(
                    it,
                    preferences.getString(LAST_FM_PASSWORD, "")!!
                )
            }
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
    }

    /*
        Must be encrypted
     */
    override fun setLastFmCredentials(user: UserCredentials) {
        preferences.edit {
            putString(LAST_FM_USERNAME, user.username)
            putString(LAST_FM_PASSWORD, user.password)
        }
    }

    override fun getSyncAdjustment(): Long {
        return preferences.getLong(SYNC_ADJUSTMENT, 0)
    }

    override fun setSyncAdjustment(value: Long) {
        preferences.edit { putLong(SYNC_ADJUSTMENT, value) }
    }

    private fun defaultFolder(): String {
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        var startFolder = File(File.separator)
        if (musicDir.exists() && musicDir.isDirectory) {
            startFolder = musicDir
        } else {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory) {
                startFolder = externalStorage
            }
        }
        return startFolder.path
    }

    override fun observeDefaultMusicFolder(): Flow<File> {
        return rxPreferences.getString(DEFAULT_MUSIC_FOLDER, defaultFolder())
            .asObservable()
            .toFlowable(BackpressureStrategy.LATEST)
            .asFlow()
            .map { File(it) }
    }

    override fun getDefaultMusicFolder(): File {
        return File(preferences.getString(DEFAULT_MUSIC_FOLDER, defaultFolder()))
    }

    override fun setDefaultMusicFolder(file: File) {
        preferences.edit {
            putString(DEFAULT_MUSIC_FOLDER, file.safeGetCanonicalPath())
        }
    }

    override fun canShowLibraryNewVisibility(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_new_albums_artists_key), true)
    }

    override fun canShowLibraryRecentPlayedVisibility(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_recent_albums_artists_key), true)
    }

    override fun canShowPodcastCategory(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_show_podcasts_key), true)
    }

    override fun isAdaptiveColorEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
    }

    override fun isLockscreenArtworkEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_lockscreen_artwork_key), false)
    }

    override fun getShowFolderAsTreeView(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getSearchFilters(): Set<SearchFilters> {
        val set = preferences.getStringSet(SEARCH_FILTERS, mutableSetOf(
                SearchFilters.ALBUM.name,
                SearchFilters.ARTIST.name,
                SearchFilters.PLAYLIST.name,
                SearchFilters.GENRE.name,
                SearchFilters.FOLDER.name
        ))!!
        return set.map { SearchFilters.valueOf(it) }.toSet()
    }

    override fun observeSearchFilters(): Flow<Set<SearchFilters>> {
        return rxPreferences.getStringSet(SEARCH_FILTERS, mutableSetOf(
                SearchFilters.ALBUM.name,
                SearchFilters.ARTIST.name,
                SearchFilters.PLAYLIST.name,
                SearchFilters.GENRE.name,
                SearchFilters.FOLDER.name
        )).asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .asFlow()
                .map { it.map { SearchFilters.valueOf(it) }.toSet() }
    }

    override fun setSearchFilters(filters: Set<SearchFilters>) {
        val asString = filters.map { it.name }.toSet()
        preferences.edit {
            putStringSet(SEARCH_FILTERS, asString)
        }
    }
}