package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.track.Song

interface SearchGateway {

    /**
     * Songs + podcast
     */
    suspend fun searchTracksBy(word: String, vararg columns: By): PageRequest<Song>
    suspend fun searchTrackInGenre(genre: String): List<Song>?

    enum class By {
        ANY,
        TITLE,
        ARTIST,
        ALBUM
    }

}