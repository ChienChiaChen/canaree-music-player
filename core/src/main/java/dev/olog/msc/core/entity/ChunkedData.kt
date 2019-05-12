package dev.olog.msc.core.entity

import kotlinx.coroutines.flow.Flow

/**
 * Wrapper data class for paging
 * @param chunkOf lambda that returns a chunk of data by (offset, limit)
 * @param allDataSize need to provide all underlying data size
 * @param observeChanges callback used to invalidate data and requery
 */
class ChunkedData<T>(
    val chunkOf: (ChunkRequest) -> List<T>,
    val allDataSize: Int,
    val observeChanges: suspend () -> Flow<Unit>
)
class ChunkRequest(
    val offset: Int,
    val limit: Int
)