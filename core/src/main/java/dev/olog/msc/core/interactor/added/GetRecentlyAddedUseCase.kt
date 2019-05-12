package dev.olog.msc.core.interactor.added

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.all.GetAllPodcastUseCase
import dev.olog.msc.core.interactor.all.GetAllSongsUseCase
import dev.olog.msc.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val TWO_WEEKS = TimeUnit.MILLISECONDS.convert(14, TimeUnit.DAYS)

internal suspend fun getRecentlyAddedSong(getAllSongsUseCase: GetAllSongsUseCase)
        : Flow<List<Song>> {

    val time = System.currentTimeMillis()
    return getAllSongsUseCase.execute()
        .map { songList ->
            songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS }
        }
}

internal suspend fun getRecentlyAddedPodcast(getAllPodcastsUseCase: GetAllPodcastUseCase)
        : Flow<List<Podcast>> {

    val time = System.currentTimeMillis()
    return getAllPodcastsUseCase.execute()
        .map { songList ->
            songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS }
        }
}

class GetRecentlyAddedUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) : ObservableUseCaseWithParam<List<Song>, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Song>> {
        val time = System.currentTimeMillis()
        if (mediaId.isFolder || mediaId.isGenre) {
            return getSongListByParamUseCase.execute(mediaId)
                .map { if (it.size >= 5) it else listOf() }
                .map { songList -> songList.filter { (time - it.dateAdded * 1000) <= TWO_WEEKS } }
        }
        return Observable.just(listOf())
    }
}