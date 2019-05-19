package dev.olog.msc.data.db

import androidx.room.*
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.*
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.data.entity.RecentSearchesEntity
import dev.olog.msc.shared.RecentSearchesTypes.ALBUM
import dev.olog.msc.shared.RecentSearchesTypes.ARTIST
import dev.olog.msc.shared.RecentSearchesTypes.FOLDER
import dev.olog.msc.shared.RecentSearchesTypes.GENRE
import dev.olog.msc.shared.RecentSearchesTypes.PLAYLIST
import dev.olog.msc.shared.RecentSearchesTypes.PODCAST
import dev.olog.msc.shared.RecentSearchesTypes.PODCAST_ALBUM
import dev.olog.msc.shared.RecentSearchesTypes.PODCAST_ARTIST
import dev.olog.msc.shared.RecentSearchesTypes.PODCAST_PLAYLIST
import dev.olog.msc.shared.RecentSearchesTypes.SONG
import dev.olog.msc.shared.utils.assertBackgroundThread

@Dao
internal abstract class RecentSearchesDao {

    @Query(
        """
        SELECT * FROM recent_searches
        ORDER BY insertionTime DESC
        LIMIT 50
    """
    )
    internal abstract fun getAllImpl(): List<RecentSearchesEntity>

    internal fun getAll(
        songGateway: SongGateway,
        albumGateway: AlbumGateway,
        artistGateway: ArtistGateway,
        playlistGateway: PlaylistGateway,
        genreGateway: GenreGateway,
        folderGateway: FolderGateway,
        podcastGateway: PodcastGateway,
        podcastPlaylistGateway: PodcastPlaylistGateway,
        podcastAlbumGateway: PodcastAlbumGateway,
        podcastArtistList: PodcastArtistGateway
    ): List<SearchResult> {
        assertBackgroundThread()

        val recentSearches = getAllImpl()

        val result = mutableListOf<Pair<SearchResult, Long>>()
        for ((key, value) in recentSearches.groupBy { it.dataType }) {
            when (key) {
                SONG -> {
                    val songs = songGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchSongMapper(
                            search,
                            songs.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                ALBUM -> {
                    val albums = albumGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchAlbumMapper(
                            search,
                            albums.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                ARTIST -> {
                    val artists = artistGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchArtistMapper(
                            search,
                            artists.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                PLAYLIST -> {
                    val playlists = playlistGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchPlaylistMapper(
                            search,
                            playlists.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                GENRE -> {
                    val genres = genreGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchGenreMapper(
                            search,
                            genres.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                FOLDER -> {
                    val folders = folderGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchFolderMapper(
                            search,
                            folders.first { it.path.hashCode().toLong() == search.itemId }
                        ) to search.insertionTime
                    })
                }
                PODCAST -> {
                    val podcasts = podcastGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchPodcastMapper(
                            search,
                            podcasts.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                PODCAST_PLAYLIST -> {
                    val podcastsPlaylist =
                        podcastPlaylistGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchPodcastPlaylistMapper(
                            search,
                            podcastsPlaylist.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                PODCAST_ALBUM -> {
                    val podcastsAlbum =
                        podcastAlbumGateway.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchPodcastAlbumMapper(
                            search,
                            podcastsAlbum.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                PODCAST_ARTIST -> {
                    val podcastsArtist =
                        podcastArtistList.getAll().getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
                    result.addAll(value.map { search ->
                        searchPodcastArtistMapper(
                            search,
                            podcastsArtist.first { it.id == search.itemId }
                        ) to search.insertionTime
                    })
                }
                else -> throw IllegalArgumentException("invalid recent element type ${key}")
            }
        }
        return result.sortedByDescending { it.second }.map { it.first }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertImpl(recent: RecentSearchesEntity)

    @Query("DELETE FROM recent_searches WHERE dataType = :dataType AND itemId = :itemId")
    internal abstract suspend fun deleteImpl(dataType: Int, itemId: Long)

    @Query("DELETE FROM recent_searches")
    internal abstract suspend fun deleteAllImpl()

    internal open suspend fun deleteSong(itemId: Long) {
        deleteImpl(SONG, itemId)
    }

    internal open suspend fun deleteAlbum(itemId: Long) {
        deleteImpl(ALBUM, itemId)
    }

    internal open suspend fun deleteArtist(itemId: Long) {
        deleteImpl(ARTIST, itemId)
    }

    internal open suspend fun deletePlaylist(itemId: Long) {
        deleteImpl(PLAYLIST, itemId)
    }

    internal open suspend fun deleteGenre(itemId: Long) {
        deleteImpl(GENRE, itemId)
    }

    internal open suspend fun deleteFolder(itemId: Long) {
        deleteImpl(FOLDER, itemId)
    }

    internal open suspend fun deletePodcast(podcastid: Long) {
        deleteImpl(PODCAST, podcastid)
    }

    internal open suspend fun deletePodcastPlaylist(playlistId: Long) {
        deleteImpl(PODCAST_PLAYLIST, playlistId)
    }

    internal open suspend fun deletePodcastArtist(artistId: Long) {
        deleteImpl(PODCAST_ARTIST, artistId)
    }

    internal open suspend fun deletePodcastAlbum(albumId: Long) {
        deleteImpl(PODCAST_ALBUM, albumId)
    }

    internal open suspend fun deleteAll() {
        deleteAllImpl()
    }

    @Transaction
    internal open suspend fun insertSong(songId: Long) {
        deleteSong(songId)
        insertImpl(RecentSearchesEntity(dataType = SONG, itemId = songId))
    }

    @Transaction
    internal open suspend fun insertAlbum(albumId: Long) {
        deleteAlbum(albumId)
        insertImpl(RecentSearchesEntity(dataType = ALBUM, itemId = albumId))
    }

    @Transaction
    internal open suspend fun insertArtist(artistId: Long) {
        deleteArtist(artistId)
        insertImpl(RecentSearchesEntity(dataType = ARTIST, itemId = artistId))
    }

    @Transaction
    internal open suspend fun insertPlaylist(playlistId: Long) {
        deletePlaylist(playlistId)
        insertImpl(RecentSearchesEntity(dataType = PLAYLIST, itemId = playlistId))
    }

    @Transaction
    internal open suspend fun insertGenre(genreId: Long) {
        deleteGenre(genreId)
        insertImpl(RecentSearchesEntity(dataType = GENRE, itemId = genreId))
    }

    @Transaction
    internal open suspend fun insertFolder(folderId: Long) {
        deleteFolder(folderId)
        insertImpl(RecentSearchesEntity(dataType = FOLDER, itemId = folderId))
    }


    @Transaction
    internal open suspend fun insertPodcast(podcastId: Long) {
        deletePodcast(podcastId)
        insertImpl(RecentSearchesEntity(dataType = PODCAST, itemId = podcastId))
    }

    internal open suspend fun insertPodcastPlaylist(playlistId: Long) {
        deletePodcastPlaylist(playlistId)
        insertImpl(RecentSearchesEntity(dataType = PODCAST_PLAYLIST, itemId = playlistId))
    }

    internal open suspend fun insertPodcastAlbum(albumId: Long) {
        deletePodcastAlbum(albumId)
        insertImpl(RecentSearchesEntity(dataType = PODCAST_ALBUM, itemId = albumId))
    }

    internal open suspend fun insertPodcastArtist(artistId: Long) {
        deletePodcastArtist(artistId)
        insertImpl(RecentSearchesEntity(dataType = PODCAST_ARTIST, itemId = artistId))
    }

    private fun searchSongMapper(recentSearch: RecentSearchesEntity, song: Song): SearchResult {
        return SearchResult(
            MediaId.songId(song.id), recentSearch.dataType,
            song.title
        )
    }

    private fun searchAlbumMapper(recentSearch: RecentSearchesEntity, album: Album): SearchResult {
        return SearchResult(
            MediaId.albumId(album.id), recentSearch.dataType,
            album.title
        )
    }

    private fun searchArtistMapper(recentSearch: RecentSearchesEntity, artist: Artist): SearchResult {
        return SearchResult(
            MediaId.artistId(artist.id), recentSearch.dataType,
            artist.name
        )
    }

    private fun searchPlaylistMapper(recentSearch: RecentSearchesEntity, playlist: Playlist): SearchResult {
        return SearchResult(
            MediaId.playlistId(playlist.id), recentSearch.dataType,
            playlist.title
        )
    }

    private fun searchGenreMapper(recentSearch: RecentSearchesEntity, genre: Genre): SearchResult {
        return SearchResult(
            MediaId.genreId(genre.id), recentSearch.dataType,
            genre.name
        )
    }

    private fun searchFolderMapper(recentSearch: RecentSearchesEntity, folder: Folder): SearchResult {
        return SearchResult(
            MediaId.folderId(folder.path), recentSearch.dataType,
            folder.title
        )
    }

    private fun searchPodcastMapper(recentSearch: RecentSearchesEntity, podcast: Podcast): SearchResult {
        return SearchResult(
            MediaId.podcastId(podcast.id), recentSearch.dataType,
            podcast.title
        )
    }

    private fun searchPodcastPlaylistMapper(
        recentSearch: RecentSearchesEntity,
        playlist: PodcastPlaylist
    ): SearchResult {
        return SearchResult(
            MediaId.podcastPlaylistId(playlist.id), recentSearch.dataType,
            playlist.title
        )
    }

    private fun searchPodcastAlbumMapper(recentSearch: RecentSearchesEntity, album: PodcastAlbum): SearchResult {
        return SearchResult(
            MediaId.podcastAlbumId(album.id), recentSearch.dataType,
            album.title
        )
    }

    private fun searchPodcastArtistMapper(recentSearch: RecentSearchesEntity, artist: PodcastArtist): SearchResult {
        return SearchResult(
            MediaId.podcastArtistId(artist.id), recentSearch.dataType,
            artist.name
        )
    }

}