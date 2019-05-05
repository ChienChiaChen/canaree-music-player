package dev.olog.msc.data.db

import androidx.room.*
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.SearchResult
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.podcast.PodcastAlbum
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.podcast.PodcastPlaylist
import dev.olog.msc.core.entity.track.*
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
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable

@Dao
internal abstract class RecentSearchesDao {

    @Query("""
        SELECT * FROM recent_searches
        ORDER BY insertionTime DESC
        LIMIT 50
    """)
    internal abstract fun getAllImpl(): Flowable<List<RecentSearchesEntity>>

    internal fun getAll(songList: Single<List<Song>>,
               albumList: Single<List<Album>>,
               artistList: Single<List<Artist>>,
               playlistList: Single<List<Playlist>>,
               genreList: Single<List<Genre>>,
               folderList: Single<List<Folder>>,
               podcastList: Single<List<Podcast>>,
               podcastPlaylistList: Single<List<PodcastPlaylist>>,
               podcastAlbumList: Single<List<PodcastAlbum>>,
               podcastArtistList: Single<List<PodcastArtist>>) : Observable<List<SearchResult>> {

        return getAllImpl()
                .toObservable()
                .flatMapSingle {  all -> all.toFlowable().concatMapMaybe { recentEntity ->
                        when (recentEntity.dataType) {
                            SONG -> songList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchSongMapper(recentEntity, it) }
                                    .firstElement()
                            ALBUM -> albumList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchAlbumMapper(recentEntity, it) }
                                    .firstElement()
                            ARTIST -> artistList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchArtistMapper(recentEntity, it) }
                                    .firstElement()
                            PLAYLIST -> playlistList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchPlaylistMapper(recentEntity, it) }
                                    .firstElement()
                            GENRE -> genreList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchGenreMapper(recentEntity, it) }
                                    .firstElement()
                            FOLDER -> folderList.flattenAsFlowable { it }
                                    .filter { it.path.hashCode().toLong() == recentEntity.itemId }
                                    .map { searchFolderMapper(recentEntity, it) }
                                    .firstElement()
                            PODCAST -> podcastList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchPodcastMapper(recentEntity, it) }
                                    .firstElement()
                            PODCAST_PLAYLIST -> podcastPlaylistList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchPodcastPlaylistMapper(recentEntity, it) }
                                    .firstElement()
                            PODCAST_ALBUM -> podcastAlbumList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchPodcastAlbumMapper(recentEntity, it) }
                                    .firstElement()
                            PODCAST_ARTIST -> podcastArtistList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchPodcastArtistMapper(recentEntity, it) }
                                    .firstElement()
                            else -> throw IllegalArgumentException("invalid recent element type ${recentEntity.dataType}")
                        } }.toList()
                }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(recent: RecentSearchesEntity)

    @Delete
    internal abstract fun deleteImpl(recentSearch: RecentSearchesEntity)

    @Query("DELETE FROM recent_searches WHERE dataType = :dataType AND itemId = :itemId")
    internal abstract fun deleteImpl(dataType: Int, itemId: Long)

    @Query("DELETE FROM recent_searches")
    internal abstract fun deleteAllImpl()

    internal open fun deleteSong(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(SONG, itemId) }
    }

    internal open fun deleteAlbum(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ALBUM, itemId) }
    }

    internal open fun deleteArtist(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ARTIST, itemId) }
    }

    internal open fun deletePlaylist(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PLAYLIST, itemId) }
    }

    internal open fun deleteGenre(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(GENRE, itemId) }
    }

    internal open fun deleteFolder(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(FOLDER, itemId) }
    }

    internal open fun deletePodcast(podcastid: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST, podcastid) }
    }

    internal open fun deletePodcastPlaylist(playlistId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_PLAYLIST, playlistId) }
    }

    internal open fun deletePodcastArtist(artistId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_ARTIST, artistId) }
    }

    internal open fun deletePodcastAlbum(albumId: Long): Completable {
        return Completable.fromCallable { deleteImpl(PODCAST_ALBUM, albumId) }
    }

    internal open fun deleteAll(): Completable {
        return Completable.fromCallable { deleteAllImpl() }
    }

    internal open fun insertSong(songId: Long): Completable{
        return deleteSong(songId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = SONG, itemId = songId)) }
    }

    internal open fun insertAlbum(albumId: Long): Completable{
        return deleteAlbum(albumId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = ALBUM, itemId = albumId)) }
    }

    internal open fun insertArtist(artistId: Long): Completable{
        return deleteArtist(artistId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = ARTIST, itemId = artistId)) }
    }

    internal open fun insertPlaylist(playlistId: Long): Completable{
        return deletePlaylist(playlistId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = PLAYLIST, itemId = playlistId)) }
    }

    internal open fun insertGenre(genreId: Long): Completable{
        return deleteGenre(genreId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = GENRE, itemId = genreId)) }
    }

    internal open fun insertFolder(folderId: Long): Completable{
        return deleteFolder(folderId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = FOLDER, itemId = folderId)) }
    }


    internal open fun insertPodcast(podcastId: Long): Completable{
        return deletePodcast(podcastId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = PODCAST, itemId = podcastId)) }
    }

    internal open fun insertPodcastPlaylist(playlistId: Long): Completable{
        return deletePodcastPlaylist(playlistId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = PODCAST_PLAYLIST, itemId = playlistId)) }
    }

    internal open fun insertPodcastAlbum(albumId: Long): Completable{
        return deletePodcastAlbum(albumId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = PODCAST_ALBUM, itemId = albumId)) }
    }

    internal open fun insertPodcastArtist(artistId: Long): Completable{
        return deletePodcastArtist(artistId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = PODCAST_ARTIST, itemId = artistId)) }
    }

    private fun searchSongMapper(recentSearch: RecentSearchesEntity, song: Song) : SearchResult {
        return SearchResult(MediaId.songId(song.id), recentSearch.dataType,
                song.title, song.image)
    }

    private fun searchAlbumMapper(recentSearch: RecentSearchesEntity, album: Album) : SearchResult {
        return SearchResult(MediaId.albumId(album.id), recentSearch.dataType,
                album.title, album.image)
    }

    private fun searchArtistMapper(recentSearch: RecentSearchesEntity, artist: Artist) : SearchResult {
        return SearchResult(MediaId.artistId(artist.id), recentSearch.dataType,
                artist.name, artist.image)
    }

    private fun searchPlaylistMapper(recentSearch: RecentSearchesEntity, playlist: Playlist) : SearchResult {
        return SearchResult(MediaId.playlistId(playlist.id), recentSearch.dataType,
                playlist.title, playlist.image)
    }

    private fun searchGenreMapper(recentSearch: RecentSearchesEntity, genre: Genre) : SearchResult {
        return SearchResult(MediaId.genreId(genre.id), recentSearch.dataType,
                genre.name, genre.image)
    }

    private fun searchFolderMapper(recentSearch: RecentSearchesEntity, folder: Folder) : SearchResult {
        return SearchResult(MediaId.folderId(folder.path), recentSearch.dataType,
                folder.title, folder.image)
    }

    private fun searchPodcastMapper(recentSearch: RecentSearchesEntity, podcast: Podcast) : SearchResult {
        return SearchResult(MediaId.podcastId(podcast.id), recentSearch.dataType,
                podcast.title, podcast.image)
    }

    private fun searchPodcastPlaylistMapper(recentSearch: RecentSearchesEntity, playlist: PodcastPlaylist) : SearchResult {
        return SearchResult(MediaId.podcastPlaylistId(playlist.id), recentSearch.dataType,
                playlist.title, playlist.image)
    }

    private fun searchPodcastAlbumMapper(recentSearch: RecentSearchesEntity, album: PodcastAlbum) : SearchResult {
        return SearchResult(MediaId.podcastAlbumId(album.id), recentSearch.dataType,
                album.title, album.image)
    }

    private fun searchPodcastArtistMapper(recentSearch: RecentSearchesEntity, artist: PodcastArtist) : SearchResult {
        return SearchResult(MediaId.podcastArtistId(artist.id), recentSearch.dataType,
                artist.name, artist.image)
    }

}