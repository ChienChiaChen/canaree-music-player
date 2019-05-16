package dev.olog.msc.core.interactor

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.coroutines.ObservableFlowWithParam
import dev.olog.msc.core.interactor.item.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val getFolderUseCase: GetFolderUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getSongUseCase: GetSongUseCase,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getGenreUseCase: GetGenreUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getPodcastPlaylistUseCase: GetPodcastPlaylistUseCase,
    private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
    private val getPodcastArtistUseCase: GetPodcastArtistUseCase

) : ObservableFlowWithParam<String?, MediaId>(schedulers) {


    override suspend fun buildUseCaseObservable(param: MediaId): Flow<String?> {
        return when (param.category) {
            MediaIdCategory.FOLDERS -> getFolderUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.PLAYLISTS -> getPlaylistUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.SONGS -> getSongUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.ALBUMS -> getAlbumUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.ARTISTS -> getArtistUseCase.execute(param).observeItem().map { it?.name }
            MediaIdCategory.GENRES -> getGenreUseCase.execute(param).observeItem().map { it?.name }
            MediaIdCategory.PODCASTS_PLAYLIST -> getPodcastPlaylistUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.PODCASTS -> getPodcastUseCase.execute(param).observeItem().map { it?.title }
            MediaIdCategory.PODCASTS_ARTISTS -> getPodcastArtistUseCase.execute(param).observeItem().map { it?.name }
            MediaIdCategory.PODCASTS_ALBUMS -> getPodcastAlbumUseCase.execute(param).observeItem().map { it?.title }
            else -> throw IllegalArgumentException("invalid media category ${param.category}")
        }
    }
}