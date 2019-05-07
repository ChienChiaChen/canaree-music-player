package dev.olog.msc.presentation.detail.sort

import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.entity.sort.SortType

data class DetailSort(
        val sortType: SortType,
        val sortArranging: SortArranging
)