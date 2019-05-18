package dev.olog.msc.core.entity.data.request

class Request(
    val page: Page,
    val filter: Filter
)

fun Request.with(page: Page? = null, filter: Filter? = null): Request {
    return Request(page ?: this.page, filter ?: this.filter)
}