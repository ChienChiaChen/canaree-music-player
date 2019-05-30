@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.extensions

import android.os.Build
import android.text.Html
import android.text.Spanned

inline fun String.asHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}