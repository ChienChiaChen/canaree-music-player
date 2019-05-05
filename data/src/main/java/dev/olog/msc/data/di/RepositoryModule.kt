package dev.olog.msc.data.di

import dagger.Binds
import dagger.Module
import dev.olog.msc.core.gateway.*
import dev.olog.msc.data.repository.*
import dev.olog.msc.data.repository.lyrics.OfflineLyricsRepository
import dev.olog.msc.data.repository.podcast.PlaylistPodcastRepository
import dev.olog.msc.data.repository.podcast.PodcastAlbumRepository
import dev.olog.msc.data.repository.podcast.PodcastArtistRepository
import dev.olog.msc.data.repository.podcast.PodcastRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun provideFolderRepository(repository: FolderRepository): FolderGateway

    @Binds
    @Singleton
    internal abstract fun providePlaylistRepository(repository: PlaylistRepository): PlaylistGateway

    @Binds
    @Singleton
    internal abstract fun provideSongRepository(repository: SongRepository): SongGateway

    @Binds
    @Singleton
    internal abstract fun provideAlbumRepository(repository: AlbumRepository): AlbumGateway

    @Binds
    @Singleton
    internal abstract fun provideArtistRepository(repository: ArtistRepository): ArtistGateway

    @Binds
    @Singleton
    internal abstract fun provideGenreRepository(repository: GenreRepository): GenreGateway

    @Binds
    @Singleton
    internal abstract fun providePodcastRepository(repository: PodcastRepository): PodcastGateway

    @Binds
    @Singleton
    internal abstract fun providePodcastPlaylistRepository(repository: PlaylistPodcastRepository): PodcastPlaylistGateway

    @Binds
    @Singleton
    internal abstract fun providePodcastAlbumsRepository(repository: PodcastAlbumRepository): PodcastAlbumGateway

    @Binds
    @Singleton
    internal abstract fun providePodcastArtistsRepository(repository: PodcastArtistRepository): PodcastArtistGateway

    @Binds
    @Singleton
    internal abstract fun providePlayingQueueRepository(repository: PlayingQueueRepository): PlayingQueueGateway

    @Binds
    @Singleton
    internal abstract fun provideFavoriteRepository(repository: FavoriteRepository): FavoriteGateway

    @Binds
    @Singleton
    internal abstract fun provideRecentSearchesRepository(repository: RecentSearchesRepository): RecentSearchesGateway

    @Binds
    @Singleton
    internal abstract fun provideLyricsRepository(repository: OfflineLyricsRepository): OfflineLyricsGateway

    @Binds
    @Singleton
    internal abstract fun provideUsedImageRepository(repository: UsedImageRepository): UsedImageGateway

}