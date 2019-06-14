package dev.olog.msc.data.utils

object TextUtils {

    @JvmStatic
    fun addSpacesToDash(original: String): String{
        return original
            .trim()
            .replace("-", " - ")
            .replace("\\s+".toRegex(), " ")
    }

}