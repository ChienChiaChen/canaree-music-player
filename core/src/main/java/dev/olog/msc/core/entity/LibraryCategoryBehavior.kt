package dev.olog.msc.core.entity

import dev.olog.msc.core.MediaIdCategory

data class LibraryCategoryBehavior(
        val category: MediaIdCategory,
        var visible: Boolean,
        var order: Int
)