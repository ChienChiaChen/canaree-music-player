package dev.olog.msc.presentation.library.folder.tree

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.core.MediaId
import java.io.File

data class DisplayableFile(
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
        val path: String?
) :BaseModel {

    fun isFile(): Boolean = path != null
    fun asFile(): File = File(path)

}