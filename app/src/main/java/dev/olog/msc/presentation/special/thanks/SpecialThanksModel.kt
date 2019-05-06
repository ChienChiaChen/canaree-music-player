package dev.olog.msc.presentation.special.thanks

import dev.olog.msc.core.MediaId
import dev.olog.presentation.base.model.BaseModel

data class SpecialThanksModel(
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val image: Int
) : BaseModel