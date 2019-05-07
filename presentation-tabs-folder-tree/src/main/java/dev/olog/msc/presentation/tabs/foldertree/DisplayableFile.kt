package dev.olog.msc.presentation.tabs.foldertree

import dev.olog.msc.core.MediaId
import dev.olog.presentation.base.model.BaseModel
import java.io.File

data class DisplayableFile(
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
        val path: String?
) : BaseModel {

    fun isFile(): Boolean = path != null
    fun asFile(): File = File(path)

}