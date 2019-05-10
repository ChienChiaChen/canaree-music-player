package dev.olog.msc.presentation.related.artists.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetArtistUseCase
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.collator
import dev.olog.msc.shared.extensions.safeCompare
import io.reactivex.Observable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class GetRelatedArtistsUseCase @Inject constructor(
        private val executors: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getArtistUseCase: GetArtistUseCase

) : ObservableUseCaseWithParam<List<Artist>, MediaId>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Artist>> {
        if (mediaId.isFolder || mediaId.isPlaylist || mediaId.isGenre){
            return getSongListByParamUseCase.execute(mediaId)
                    .switchMapSingle { songList -> songList.toFlowable()
                            .filter { it.artist != TrackUtils.UNKNOWN }
                            .distinct { it.artistId }
                            .map { MediaId.artistId(it.artistId) }
                            .flatMapSingle { getArtistUseCase.execute(it).firstOrError().subscribeOn(executors.worker) }
                            .toSortedList { o1, o2 -> collator.safeCompare(o1.name, o2.name) }
                    }
        }
        return Observable.just(emptyList())
    }
}