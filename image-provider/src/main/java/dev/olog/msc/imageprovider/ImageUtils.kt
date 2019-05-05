package dev.olog.msc.imageprovider

import java.io.File

object ImageUtils {

    fun isRealImage(image: String): Boolean {
        return File(image).exists()
    }

}