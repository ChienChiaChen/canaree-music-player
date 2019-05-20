package dev.olog.msc.floatingwindowservice

import android.content.Context

internal class VideoContent(
    context: Context

) : WebViewContent(context, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}