package dev.olog.msc.glide.creator

import android.content.Context
import java.io.File

object ImagesFolderUtils {

    const val FOLDER = "folder"
    const val PLAYLIST = "playlist"
    const val GENRE = "genre"

    fun getImageFolderFor(context: Context, entity: String): File {
        val folder = File("${context.applicationInfo.dataDir}${File.separator}$entity")
        if (!folder.exists()){
            folder.mkdir()
        }
        return folder
    }

    fun createFileName(itemId: String, progressive: Long, albumsId: List<Long>): String {
        val albumsIdAsString = albumsId.joinToString(
            separator = "_",
            prefix = "(",
            postfix = ")"
        )

        val builder = StringBuilder() // using a builder for readability
            .append(itemId)
            .append("_")
            .append(progressive)
            .append(albumsIdAsString)
        return builder.toString()
    }

}

fun File.extractImageName(): ImageName {
    return ImageName(this)
}

/**
 * File name structure -> artistId_progressive(albumsIdSeparatedByUnderscores).webp
 */
class ImageName(file: File) {

    private val name = file.name

    fun containedAlbums(): List<Long> {
        val indexOfStart = name.indexOf("(") + 1
        val indexOfEnd = name.indexOf(")")
        return name.substring(indexOfStart, indexOfEnd)
                .split("_")
                .map { it.toLong() }
    }

    fun progressive(): Long {
        val indexOfStart = name.indexOf("_") + 1
        val indexOfEnd = name.indexOf("(")
        return name.substring(indexOfStart, indexOfEnd).toLong()
    }

}
