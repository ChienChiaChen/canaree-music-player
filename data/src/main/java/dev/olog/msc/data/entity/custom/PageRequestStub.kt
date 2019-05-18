package dev.olog.msc.data.entity.custom

import dev.olog.msc.core.entity.data.request.DataRequest
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PageRequestStub<T> : DataRequest<T> {
    override fun getPage(request: Request): List<T> {
        return listOf()
    }

    override fun getCount(filter: Filter): Int {
        return 0
    }

    override suspend fun observePage(page: Request): Flow<List<T>> {
        return flowOf()
    }

    override suspend fun observeNotification(): Flow<Unit> {
        return flowOf()
    }
}