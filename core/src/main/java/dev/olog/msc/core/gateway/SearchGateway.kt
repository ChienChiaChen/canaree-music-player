package dev.olog.msc.core.gateway

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song

interface SearchGateway {

    /**
     * Songs + podcast
     */
    fun searchSongsAndPocastsBy(request: SearchRequest): DataRequest<Song>
    fun searchSongOnlyBy(request: SearchRequest): DataRequest<Song>
    fun searchPodcastOnlyBy(request: SearchRequest): DataRequest<Podcast>
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