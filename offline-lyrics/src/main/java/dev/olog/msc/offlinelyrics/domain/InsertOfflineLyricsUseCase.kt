package dev.olog.msc.offlinelyrics.domain

import android.util.Log
import dev.olog.msc.core.entity.OfflineLyrics
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.OfflineLyricsGateway
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.core.interactor.base.CompletableFlowWithParam
import java.io.File
import javax.inject.Inject

class InsertOfflineLyricsUseCase @Inject constructor(
    executors: ComputationDispatcher,
    private val gateway: OfflineLyricsGateway,
    private val songGateway: SongGateway,
    private val lyricsFromMetadata: ILyricsFromMetadata

) : CompletableFlowWithParam<OfflineLyrics>(executors) {

    override suspend fun buildUseCaseObservable(offlineLyrics: OfflineLyrics) {
        val song = songGateway.getByParam(offlineLyrics.trackId).getItem()
        if (song == null) {
            Log.w("InsertLyricsInteractor", "Track id=${offlineLyrics.trackId} not found")
            return
        }
        try {
            saveLyricsOnMetadata(song, offlineLyrics.lyrics)
        } catch (ex: Exception) {
            Log.w("InsertLyricsInteractor", "Can't save lyrics on track metadata for=$song")
        }

        gateway.saveLyrics(offlineLyrics)
    }

    private fun saveLyricsOnMetadata(song: Song, lyrics: String) {
        lyricsFromMetadata.setLyrics(song, lyrics)
        updateFileIfAny(song.path, lyrics)
    }

    private fun updateFileIfAny(path: String, lyrics: String) {
        val file = File(path)
        val fileName = file.nameWithoutExtension
        val lyricsFile = File(file.parentFile, "$fileName.lrc")

        if (lyricsFile.exists()) {
            lyricsFile.printWriter().use { out ->
                val lines = lyrics.split("\n")
                lines.forEach {
                    out.println(it)
                }
            }
        }
    }

}