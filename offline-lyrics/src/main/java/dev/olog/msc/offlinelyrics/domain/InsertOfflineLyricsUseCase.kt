package dev.olog.msc.offlinelyrics.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.OfflineLyrics
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.core.interactor.item.GetSongUseCase
import io.reactivex.Completable
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
        executors: IoScheduler,
        private val gateway: OfflineLyricsGateway,
        private val getSongUseCase: GetSongUseCase,
        private val lyricsFromMetadata: ILyricsFromMetadata

) : CompletableUseCaseWithParam<OfflineLyrics>(executors) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(offlineLyrics: OfflineLyrics): Completable {
        return getSongUseCase.execute(MediaId.songId(offlineLyrics.trackId))
                .firstOrError()
                .flatMapCompletable { saveLyricsOnMetadata(it, offlineLyrics.lyrics) }
                .andThen(gateway.saveLyrics(offlineLyrics))
                .onErrorResumeNext { gateway.saveLyrics(offlineLyrics) }
    }

    private fun saveLyricsOnMetadata(song: Song, lyrics: String): Completable {
        return Completable.create {
            lyricsFromMetadata.setLyrics(song, lyrics)
            updateFileIfAny(song.path, lyrics)

            it.onComplete()
        }
    }

    private fun updateFileIfAny(path: String, lyrics: String){
        val file = File(path)
        val fileName = file.nameWithoutExtension
        val lyricsFile = File(file.parentFile, "$fileName.lrc")

        if (lyricsFile.exists()){
            lyricsFile.printWriter().use { out ->
                val lines = lyrics.split("\n")
                lines.forEach {
                    out.println(it)
                }
            }
        }
    }

}