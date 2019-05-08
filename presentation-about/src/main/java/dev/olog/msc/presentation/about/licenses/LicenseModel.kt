package dev.olog.msc.presentation.about.licenses

import dev.olog.msc.core.MediaId
import dev.olog.presentation.base.model.BaseModel

data class LicenseModel(
        override val type: Int,
        override val mediaId: MediaId,
        val name: String,
        val url: String,
        val license: String

) : BaseModel