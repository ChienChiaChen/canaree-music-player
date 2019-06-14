package dev.olog.msc.dagger.qualifier

import dagger.MapKey
import dev.olog.msc.core.MediaIdCategory

@MapKey
annotation class MediaIdCategoryKey(
        val value: MediaIdCategory
)
