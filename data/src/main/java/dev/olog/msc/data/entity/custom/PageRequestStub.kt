package dev.olog.msc.data.entity.custom

import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.entity.PageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PageRequestStub<T> : PageRequest<T> {
    override fun getPage(page: Page): List<T> {
        return listOf()
    }

    override fun getCount(): Int {
        return 0
    }

    override suspend fun observePage(page: Page): Flow<List<T>> {
        return flowOf()
    }

    override suspend fun observeNotification(): Flow<Unit> {
        return flowOf()
    }
}