package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.data.entity.PodcastPlaylistEntity
import dev.olog.msc.data.entity.PodcastPlaylistTrackEntity
import io.reactivex.Flowable

@Dao
internal abstract class PodcastPlaylistDao {

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """
    )
    internal abstract fun observeAll(): Flowable<List<PodcastPlaylistEntity>>

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        GROUP BY playlistId
    """
    )
    internal abstract fun observeById(playlistId: Long): Flowable<PodcastPlaylistEntity>

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getChunk(limit: Int, offset: Int): List<PodcastPlaylistEntity>

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId <> :playlistId
        GROUP BY playlistId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getSiblings(playlistId: Long, limit: Int, offset: Int): List<PodcastPlaylistEntity>

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId <> :playlistId
        GROUP BY playlistId
    """
    )
    internal abstract fun observeSibling(playlistId: Long): Flowable<List<PodcastPlaylistEntity>>

    @Query(
        """
        SELECT count(*) FROM (
            SELECT playlist.*, count(*) as size
            FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
                ON playlist.id = tracks.playlistId
            WHERE playlistId <> :playlistId
            GROUP BY playlistId
        )
    """
    )
    internal abstract fun countSiblings(playlistId: Long): Int

    @Query(
        """
        SELECT count(*) FROM (
            SELECT playlist.*, count(*) as size
            FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
                ON playlist.id = tracks.playlistId
            GROUP BY playlistId
        )
    """
    )
    internal abstract fun getCount(): Int

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        GROUP BY playlistId
    """
    )
    internal abstract fun getAllPlaylistsBlocking(): List<PodcastPlaylistEntity>

    @Query(
        """
        SELECT playlist.*, count(*) as size
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        where playlist.id = :id
        GROUP BY playlistId
    """
    )
    internal abstract fun getPlaylist(id: Long): PodcastPlaylistEntity

    @Query(
        """
        SELECT tracks.*
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun getPlaylistTracks(playlistId: Long, offset: Int, limit: Int)
            : List<PodcastPlaylistTrackEntity>

    @Query(
        """
        SELECT tracks.*
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
        LIMIT :limit
        OFFSET :offset
    """
    )
    internal abstract fun observePlaylistTracks(playlistId: Long, offset: Int, limit: Int)
            : Flowable<List<PodcastPlaylistTrackEntity>>

    @Query(
        """
        SELECT count(*)
        FROM (
            SELECT tracks.*
            FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
                ON playlist.id = tracks.playlistId
            WHERE playlistId = :playlistId
        )
    """
    )
    internal abstract fun countPlaylistTracks(playlistId: Long): Int

    @Query(
        """
        SELECT max(idInPlaylist)
        FROM podcast_playlist playlist JOIN podcast_playlist_tracks tracks
            ON playlist.id = tracks.playlistId
        WHERE playlistId = :playlistId
    """
    )
    internal abstract suspend fun getPlaylistMaxId(playlistId: Long): Int

    @Insert
    internal abstract suspend fun createPlaylist(playlist: PodcastPlaylistEntity): Long

    @Query(
        """
        UPDATE podcast_playlist SET name = :name WHERE id = :id
    """
    )
    internal abstract suspend fun renamePlaylist(id: Long, name: String)

    @Query("""DELETE FROM podcast_playlist WHERE id = :id""")
    internal abstract suspend fun deletePlaylist(id: Long)

    @Insert
    internal abstract suspend fun insertTracks(tracks: List<PodcastPlaylistTrackEntity>)

    @Query(
        """
        DELETE FROM podcast_playlist_tracks
        WHERE playlistId = :playlistId AND id = :idInPlaylist
    """
    )
    internal abstract suspend fun deleteTrack(playlistId: Long, idInPlaylist: Long)

    @Query(
        """
        DELETE FROM podcast_playlist_tracks WHERE playlistId = :id
    """
    )
    internal abstract suspend fun clearPlaylist(id: Long)

    @Query(
        """
        DELETE FROM podcast_playlist_tracks
        WHERE EXISTS (
            SELECT count(*) as items
            FROM podcast_playlist_tracks
            WHERE playlistId = :id
            GROUP BY id, playlistId
            HAVING items > 1
        )
    """
    )
    internal abstract suspend fun removeDuplicated(id: Long)

}