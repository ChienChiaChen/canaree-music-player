package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.podcast.toSong
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.gateway.*
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
    schedulers: ComputationScheduler,
    private val genreDataStore: GenreGateway,
    private val playlistDataStore: PlaylistGateway,
    private val albumDataStore: AlbumGateway,
    private val artistDataStore: ArtistGateway,
    private val folderDataStore: FolderGateway,
    private val songDataStore: SongGateway,
    private val podcastDataStore: PodcastGateway,
    private val podcastPlaylistDataStore: PodcastPlaylistGateway,
    private val podcastAlbumDataStore: PodcastAlbumGateway,
    private val podcastArtistDataStore: PodcastArtistGateway

) : ObservableUseCaseWithParam<List<Song>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> = runBlocking {
        if (mediaId.isAll) {
            songDataStore.getAll().asObservable()
        } else {
            when (mediaId.category) {
                MediaIdCategory.FOLDERS -> folderDataStore.observeSongListByParam(mediaId.categoryValue)
                MediaIdCategory.PLAYLISTS -> playlistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
                MediaIdCategory.SONGS -> runBlocking { songDataStore.getAll() }.asObservable()
                MediaIdCategory.ALBUMS -> albumDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
                MediaIdCategory.ARTISTS -> artistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
                MediaIdCategory.GENRES -> genreDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
                MediaIdCategory.PODCASTS -> podcastDataStore.getAll().map { it.map { it.toSong() } }.asObservable()
                MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .map { it.map { it.toSong() } }
                MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .map { it.map { it.toSong() } }
                MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .map { it.map { it.toSong() } }
                else -> throw AssertionError("invalid media id $mediaId")
            }
        }
    }


}
