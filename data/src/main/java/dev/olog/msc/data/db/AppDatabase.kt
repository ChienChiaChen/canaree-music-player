package dev.olog.msc.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.olog.msc.data.db.last.played.LastPlayedAlbumDao
import dev.olog.msc.data.db.last.played.LastPlayedArtistDao
import dev.olog.msc.data.db.last.played.LastPlayedPodcastAlbumDao
import dev.olog.msc.data.db.last.played.LastPlayedPodcastArtistDao
import dev.olog.msc.data.db.most.played.FolderMostPlayedDao
import dev.olog.msc.data.db.most.played.GenreMostPlayedDao
import dev.olog.msc.data.db.most.played.PlaylistMostPlayedDao
import dev.olog.msc.data.entity.*


@Database(entities = arrayOf(
        PlayingQueueEntity::class,
        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class,

        FavoriteEntity::class,
        FavoritePodcastEntity::class,

        RecentSearchesEntity::class,

        HistoryEntity::class,
        PodcastHistoryEntity::class,

        LastPlayedAlbumEntity::class,
        LastPlayedArtistEntity::class,
        LastPlayedPodcastAlbumEntity::class,
        LastPlayedPodcastArtistEntity::class,

        LastFmTrackEntity::class,
        LastFmAlbumEntity::class,
        LastFmArtistEntity::class,

        OfflineLyricsEntity::class,

        UsedTrackImageEntity::class,
        UsedAlbumImageEntity::class,
        UsedArtistImageEntity::class,

        PodcastPlaylistEntity::class,
        PodcastPlaylistTrackEntity::class,

        PodcastPositionEntity::class

), version = 16, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    internal abstract fun playingQueueDao(): PlayingQueueDao

    internal abstract fun folderMostPlayedDao(): FolderMostPlayedDao

    internal abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    internal abstract fun genreMostPlayedDao(): GenreMostPlayedDao

    internal abstract fun favoriteDao(): FavoriteDao

    internal abstract fun recentSearchesDao(): RecentSearchesDao

    internal abstract fun historyDao(): HistoryDao

    internal abstract fun lastPlayedAlbumDao() : LastPlayedAlbumDao
    internal abstract fun lastPlayedArtistDao() : LastPlayedArtistDao
    internal abstract fun lastPlayedPodcastArtistDao() : LastPlayedPodcastArtistDao
    internal abstract fun lastPlayedPodcastAlbumDao() : LastPlayedPodcastAlbumDao

    abstract fun lastFmDao() : LastFmDao

    internal abstract fun offlineLyricsDao(): OfflineLyricsDao

    internal abstract fun usedImageDao(): UsedImageDao

    internal abstract fun podcastPlaylistDao(): PodcastPlaylistDao

    internal abstract fun podcastPositionDao(): PodcastPositionDao
}