package dev.olog.msc.domain.entity

import dev.olog.msc.core.MediaId

data class SearchResult(
        val mediaId: MediaId,
        val itemType: Int,
        val title: String,
        val image: String
)