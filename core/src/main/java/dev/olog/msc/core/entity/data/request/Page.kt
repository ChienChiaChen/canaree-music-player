package dev.olog.msc.core.entity.data.request

class Page(
    val offset: Int,
    val limit: Int
) {

    companion object {
        val NO_PAGING = Page(0, Int.MAX_VALUE)
    }

}