package dev.olog.msc.app.injection

import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.offlinelyrics.domain.ILyricsFromMetadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

internal class LyricsFromMetadata @Inject constructor() : ILyricsFromMetadata {

    override fun getLyrics(song: Song): String {
        try {
            val file = File(song.path)

            val fileName = file.nameWithoutExtension
            val lyricsFile = File(file.parentFile, "$fileName.lrc")

            if (lyricsFile.exists()) {
                return lyricsFile.bufferedReader().use { it.readText() }
            }

            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagAndConvertOrCreateAndSetDefault
            return tag.getFirst(FieldKey.LYRICS)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return ""
        }

    }

    override fun setLyrics(song: Song, lyrics: String) {
        try {
            val file = File(song.path)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagAndConvertOrCreateAndSetDefault
            tag.setField(FieldKey.LYRICS, lyrics)
            audioFile.commit()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

    }
}