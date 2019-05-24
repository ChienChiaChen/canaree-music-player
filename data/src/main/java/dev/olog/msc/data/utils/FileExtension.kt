package dev.olog.msc.data.utils

import java.io.File

fun File.safeGetCanonicalPath(): String {
    try {
        return canonicalPath
    } catch (e: Exception) {
        e.printStackTrace()
        return absolutePath
    }

}