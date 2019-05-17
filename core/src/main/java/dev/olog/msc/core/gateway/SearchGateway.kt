package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.PageRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song

interface SearchGateway {

    /**
     * Songs + podcast
     */
    fun searchSongsAndPocastsBy(request: SearchRequest): PageRequest<Song>
    fun searchSongOnlyBy(request: SearchRequest): PageRequest<Song>
    fun searchPodcastOnlyBy(request: SearchRequest): PageRequest<Podcast>
    fun searchSongsInGenre(genre: String): List<Song>?

    enum class By {
        NO_FILTER,
        TITLE,
        ARTIST,
        ALBUM
    }

    class SearchRequest(
        val byWord: Pair<String, Array<By>>,
        val byIds: List<Long>? = null
    )

}