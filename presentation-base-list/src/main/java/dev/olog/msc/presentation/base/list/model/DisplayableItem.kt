package dev.olog.msc.presentation.base.list.model

import android.os.Bundle
import dev.olog.msc.core.MediaId

data class DisplayableItem(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String? = null,
    val isPlayable: Boolean = false,
    val trackNumber: String = "",
    val extra: Bundle? = null

) : BaseModel