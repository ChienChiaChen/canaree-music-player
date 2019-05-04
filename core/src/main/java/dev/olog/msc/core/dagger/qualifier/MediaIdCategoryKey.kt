package dev.olog.msc.core.dagger.qualifier

import dagger.MapKey
import dev.olog.msc.core.MediaIdCategory

@MapKey
annotation class MediaIdCategoryKey(
        val value: MediaIdCategory
)
