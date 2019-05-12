package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetPodcastArtistUseCase
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetPodcastRelatedArtistsUseCase @Inject constructor(
        private val executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getPodcastArtistUseCase: GetPodcastArtistUseCase

) : ObservableUseCaseWithParam<List<PodcastArtist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastArtist>> = runBlocking{
        if (mediaId.isPodcastPlaylist){
            getSongListByParamUseCase.execute(mediaId)
                    .switchMapSingle { songList -> songList.toFlowable()
                            .filter { it.artist != TrackUtils.UNKNOWN }
                            .distinct { it.artistId }
                            .map { MediaId.podcastArtistId(it.artistId) }
                            .flatMapSingle { runBlocking { getPodcastArtistUseCase.execute(it).asObservable().firstOrError() }.subscribeOn(executors.worker) }
                            .toSortedList { o1, o2 -> collator.safeCompare(o1.name, o2.name) }
                    }
        } else {
            Observable.just(emptyList())
        }
    }
}