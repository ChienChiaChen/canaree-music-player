package dev.olog.msc.utils.k.extension

import android.text.Html
import android.text.Spanned
import dev.olog.msc.shared.utils.isNougat

fun String.asHtml(): Spanned {
    return if (isNougat()){
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}