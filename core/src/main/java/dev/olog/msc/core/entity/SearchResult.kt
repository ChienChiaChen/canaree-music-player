package dev.olog.msc.core.entity

import dev.olog.msc.core.MediaId

data class SearchResult(
        val mediaId: MediaId,
        val itemType: Int,
        val title: String
)