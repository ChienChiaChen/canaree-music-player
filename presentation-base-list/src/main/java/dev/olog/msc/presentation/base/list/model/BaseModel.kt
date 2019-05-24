package dev.olog.msc.presentation.base.list.model

import dev.olog.msc.core.MediaId

interface BaseModel {
    val type: Int
    val mediaId: MediaId
}