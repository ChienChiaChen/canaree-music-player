package dev.olog.msc.presentation.edititem.artist

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.item.GetArtistUseCase
import dev.olog.msc.core.interactor.item.GetPodcastArtistUseCase
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getArtistUseCase: GetArtistUseCase,
        private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    private lateinit var originalArtist: DisplayableArtist
    lateinit var songList: List<Song>

    fun observeArtist(): Single<DisplayableArtist> {
        if (mediaId.isPodcastArtist){
            return getPodcastArtistInternal()
        }
        return getArtistInternal()
    }

    private fun getArtistInternal(): Single<DisplayableArtist> = runBlocking{
        TODO()
//        getArtistUseCase.execute(mediaId).asObservable()
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
    }

    private fun getPodcastArtistInternal(): Single<DisplayableArtist> = runBlocking{
        TODO()
//        getPodcastArtistUseCase.execute(mediaId).asObservable()
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
    }

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun getArtist(): DisplayableArtist = originalArtist

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
                this.id,
                this.name,
                this.albumArtist,
                this.image
        )
    }

    private fun PodcastArtist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
                this.id,
                this.name,
                this.albumArtist,
                this.image
        )
    }

}