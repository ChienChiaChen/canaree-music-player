package dev.olog.service.music

import com.nhaarman.mockitokotlin2.mock
import dev.olog.core.MediaId
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.SkipType
import dev.olog.test.shared.CoroutinesMainDispatcherRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EnhacedShuffleTest {

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    val enhancedShuffle = EnhancedShuffle(mock())

    @Test
    fun `test shuffle with empty list`() = runBlocking<Unit> {

        // given
        val queue = emptyList<MediaEntity>()

        val justPlayed = listOf(
            MediaEntity(
                1L, 1, MediaId.songId(1L), 1L, 1L,
                "title", "artist", "album", "album", 1,
                1, "", "", 0, 0, false
            )
        )

        // when, after playing n songs
        for (metadataEntity in justPlayed) {
            enhancedShuffle.onMetadataChanged(MetadataEntity(metadataEntity, SkipType.NONE))
        }
        // shuffle
        val shuffled = enhancedShuffle.shuffle(queue.toMutableList())

        // then check if last plaeyd song are at the end of queue
        assertEquals(
            emptyList<MediaEntity>(), // expected
            shuffled                  // actual
        )
    }

    // TODO something is not working well when 'just played' is more than half of new queue
    @Test
    fun `test shuffle`() = runBlocking<Unit> {

        // given
        val queue = (0 until 10).map {
            MediaEntity(
                it.toLong(), it, MediaId.songId(it.toLong()), it.toLong(), it.toLong(),
                "title", "artist", "album", "album", 1,
                1, "", "", 0, 0, false
            )
        }

        val justPlayed = queue.take(5)
        val notPlayedYet = queue.drop(justPlayed.size)
        // sanity check
        assertEquals(queue, justPlayed + notPlayedYet)

        // when, after playing n songs
        for (metadataEntity in justPlayed) {
            enhancedShuffle.onMetadataChanged(MetadataEntity(metadataEntity, SkipType.NONE))
        }
        // shuffle
        val shuffled = enhancedShuffle.shuffle(queue.toMutableList())

        // then check if last plaeyd song are at the end of queue
        assertEquals(
            justPlayed.take(justPlayed.size),
            shuffled.takeLast(justPlayed.size)
        )
    }

}