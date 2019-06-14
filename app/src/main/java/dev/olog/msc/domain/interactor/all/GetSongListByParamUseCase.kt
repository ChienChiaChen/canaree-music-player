package dev.olog.msc.domain.interactor.all

import dev.olog.msc.core.entity.Song
import dev.olog.msc.core.entity.toSong
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.domain.gateway.*
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable
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
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        if (mediaId.isAll){
            return songDataStore.getAll()
        }

        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderDataStore.observeSongListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songDataStore.getAll()
            MediaIdCategory.ALBUMS -> albumDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreDataStore.observeSongListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS -> podcastDataStore.getAll().mapToList { it.toSong() }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .mapToList { it.toSong() }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .mapToList { it.toSong() }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistDataStore.observePodcastListByParam(mediaId.categoryValue.toLong())
                    .mapToList { it.toSong() }
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }



}
