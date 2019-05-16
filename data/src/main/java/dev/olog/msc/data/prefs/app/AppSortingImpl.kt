package dev.olog.msc.data.prefs.app

import android.content.SharedPreferences
import androidx.core.content.edit
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.Sorting
import dev.olog.msc.shared.extensions.asFlowable
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.flow.asFlow

internal class AppSortingImpl(
    private val preferences: SharedPreferences,
    private val rxPreferences: RxSharedPreferences

) : Sorting {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val ALL_SONGS_SORT_ORDER = "$TAG.ALL_SONG_SORT_ORDER"
        private const val ALL_SONGS_SORT_ARRANGING = "$TAG.ALL_SONGS_SORT_ARRANGING"
        private const val ALL_ALBUMS_SORT_ORDER = "$TAG.ALL_ALBUMS_SORT_ORDER"
        private const val ALL_ALBUMS_SORT_ARRANGING = "$TAG.ALL_ALBUMS_SORT_ARRANGING"
        private const val ALL_ARTISTS_SORT_ORDER = "$TAG.ALL_ARTISTS_SORT_ORDER"
        private const val ALL_ARTISTS_SORT_ARRANGING = "$TAG.ALL_ARTISTS_SORT_ARRANGING"

        private const val DETAIL_SORT_FOLDER_ORDER = "$TAG.DETAIL_SORT_FOLDER_ORDER"
        private const val DETAIL_SORT_PLAYLIST_ORDER = "$TAG.DETAIL_SORT_PLAYLIST_ORDER"
        private const val DETAIL_SORT_ALBUM_ORDER = "$TAG.DETAIL_SORT_ALBUM_ORDER"
        private const val DETAIL_SORT_ARTIST_ORDER = "$TAG.DETAIL_SORT_ARTIST_ORDER"
        private const val DETAIL_SORT_GENRE_ORDER = "$TAG.DETAIL_SORT_GENRE_ORDER"

        private const val DETAIL_SORT_ARRANGING = "$TAG.DETAIL_SORT_ARRANGING"
    }

    override fun getFolderSortOrder(): Flow<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_FOLDER_ORDER, SortType.TITLE.ordinal)
            .asObservable()
            .map { ordinal -> SortType.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override fun getPlaylistSortOrder(): Flow<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_PLAYLIST_ORDER, SortType.CUSTOM.ordinal)
            .asObservable()
            .map { ordinal -> SortType.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override fun getAlbumSortOrder(): Flow<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ALBUM_ORDER, SortType.TITLE.ordinal)
            .asObservable()
            .map { ordinal -> SortType.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override fun getArtistSortOrder(): Flow<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_ARTIST_ORDER, SortType.TITLE.ordinal)
            .asObservable()
            .map { ordinal -> SortType.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override fun getGenreSortOrder(): Flow<SortType> {
        return rxPreferences.getInteger(DETAIL_SORT_GENRE_ORDER, SortType.TITLE.ordinal)
            .asObservable()
            .map { ordinal -> SortType.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override suspend fun setFolderSortOrder(sortType: SortType) {
        assertBackgroundThread()
        preferences.edit { putInt(DETAIL_SORT_FOLDER_ORDER, sortType.ordinal) }
    }

    override suspend fun setPlaylistSortOrder(sortType: SortType) {
        assertBackgroundThread()
        preferences.edit { putInt(DETAIL_SORT_PLAYLIST_ORDER, sortType.ordinal) }
    }

    override suspend fun setAlbumSortOrder(sortType: SortType) {
        assertBackgroundThread()
        preferences.edit { putInt(DETAIL_SORT_ALBUM_ORDER, sortType.ordinal) }
    }

    override suspend fun setArtistSortOrder(sortType: SortType) {
        assertBackgroundThread()
        preferences.edit { putInt(DETAIL_SORT_ARTIST_ORDER, sortType.ordinal) }
    }

    override suspend fun setGenreSortOrder(sortType: SortType) {
        assertBackgroundThread()
        preferences.edit { putInt(DETAIL_SORT_GENRE_ORDER, sortType.ordinal) }
    }

    override fun getSortArranging(): Flow<SortArranging> {
        return rxPreferences.getInteger(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
            .asObservable()
            .map { ordinal -> SortArranging.values()[ordinal] }
            .asFlowable().asFlow()
    }

    override suspend fun toggleSortArranging() {
        assertBackgroundThread()
        val oldArranging =
            SortArranging.values()[preferences.getInt(DETAIL_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)]

        val newArranging = if (oldArranging == SortArranging.ASCENDING) {
            SortArranging.DESCENDING
        } else SortArranging.ASCENDING

        preferences.edit { putInt(DETAIL_SORT_ARRANGING, newArranging.ordinal) }
    }

    override fun getAllTracksSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(ALL_SONGS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(SortType.values()[sort], SortArranging.values()[arranging])
    }

    override fun getAllAlbumsSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal)
        val arranging = preferences.getInt(ALL_ALBUMS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(SortType.values()[sort], SortArranging.values()[arranging])
    }

    override fun getAllArtistsSortOrder(): LibrarySortType {
        val sort = preferences.getInt(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal)
        val arranging = preferences.getInt(ALL_ARTISTS_SORT_ARRANGING, SortArranging.ASCENDING.ordinal)
        return LibrarySortType(SortType.values()[sort], SortArranging.values()[arranging])
    }

    override fun observeAllTracksSortOrder(): Flow<LibrarySortType> {
        return Observables.run {
            combineLatest(
                rxPreferences.getInteger(ALL_SONGS_SORT_ORDER, SortType.TITLE.ordinal).asObservable(),
                rxPreferences.getInteger(
                    ALL_SONGS_SORT_ARRANGING,
                    SortArranging.ASCENDING.ordinal
                ).asObservable() //ascending default
            ) { sort, arranging ->
                LibrarySortType(
                    SortType.values()[sort],
                    SortArranging.values()[arranging]
                )
            }.asFlowable().asFlow()
        }
    }

    override fun observeAllAlbumsSortOrder(): Flow<LibrarySortType> {
        return Observables.combineLatest(
            rxPreferences.getInteger(ALL_ALBUMS_SORT_ORDER, SortType.TITLE.ordinal).asObservable(),
            rxPreferences.getInteger(
                ALL_ALBUMS_SORT_ARRANGING,
                SortArranging.ASCENDING.ordinal
            ).asObservable() //ascending default
        ) { sort, arranging ->
            LibrarySortType(
                SortType.values()[sort],
                SortArranging.values()[arranging])
        }.asFlowable().asFlow()
    }

    override fun observeAllArtistsSortOrder(): Flow<LibrarySortType> {
        return Observables.combineLatest(
            rxPreferences.getInteger(ALL_ARTISTS_SORT_ORDER, SortType.ARTIST.ordinal).asObservable(),
            rxPreferences.getInteger(
                ALL_ARTISTS_SORT_ARRANGING,
                SortArranging.ASCENDING.ordinal
            ).asObservable() //ascending default
        ) { sort, arranging -> LibrarySortType(SortType.values()[sort], SortArranging.values()[arranging]) }
            .asFlowable().asFlow()
    }

    override suspend fun setAllTracksSortOrder(sortType: LibrarySortType) {
        assertBackgroundThread()
        preferences.edit {
            putInt(ALL_SONGS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_SONGS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

    override suspend fun setAllAlbumsSortOrder(sortType: LibrarySortType) {
        assertBackgroundThread()
        preferences.edit {
            putInt(ALL_ALBUMS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ALBUMS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

    override suspend fun setAllArtistsSortOrder(sortType: LibrarySortType) {
        assertBackgroundThread()
        preferences.edit {
            putInt(ALL_ARTISTS_SORT_ORDER, sortType.type.ordinal)
            putInt(ALL_ARTISTS_SORT_ARRANGING, sortType.arranging.ordinal)
        }
    }

}