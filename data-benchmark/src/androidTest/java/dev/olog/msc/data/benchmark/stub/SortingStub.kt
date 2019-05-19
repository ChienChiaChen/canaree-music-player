package dev.olog.msc.data.benchmark.stub

import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import kotlinx.coroutines.flow.Flow

class SortingStub : SortPreferencesGateway {
    override fun getAllTracksSortOrder(): LibrarySortType {
        return LibrarySortType(SortType.TITLE, SortArranging.ASCENDING)
    }

    override fun getAllAlbumsSortOrder(): LibrarySortType {
        return LibrarySortType(SortType.TITLE, SortArranging.ASCENDING)
    }

    override fun getAllArtistsSortOrder(): LibrarySortType {
        return LibrarySortType(SortType.ARTIST, SortArranging.ASCENDING)
    }

    override fun getDetailFolderSortOrder(): SortType {
        return SortType.TITLE
    }

    override fun getDetailPlaylistSortOrder(): SortType {
        return SortType.TITLE
    }

    override fun getDetailAlbumSortOrder(): SortType {
        return SortType.TITLE
    }

    override fun getDetailArtistSortOrder(): SortType {
        return SortType.TITLE
    }

    override fun getDetailGenreSortOrder(): SortType {
        return SortType.TITLE
    }

    override fun getDetailSortArranging(): SortArranging {
        return SortArranging.ASCENDING
    }

    override fun observeAllTracksSortOrder(): Flow<LibrarySortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeAllAlbumsSortOrder(): Flow<LibrarySortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeAllArtistsSortOrder(): Flow<LibrarySortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setAllTracksSortOrder(sortType: LibrarySortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setAllAlbumsSortOrder(sortType: LibrarySortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setAllArtistsSortOrder(sortType: LibrarySortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeFolderSortOrder(): Flow<SortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observePlaylistSortOrder(): Flow<SortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeAlbumSortOrder(): Flow<SortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeArtistSortOrder(): Flow<SortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeGenreSortOrder(): Flow<SortType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setFolderSortOrder(sortType: SortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setPlaylistSortOrder(sortType: SortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setAlbumSortOrder(sortType: SortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setArtistSortOrder(sortType: SortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setGenreSortOrder(sortType: SortType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeSortArranging(): Flow<SortArranging> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun toggleSortArranging() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}