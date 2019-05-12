package dev.olog.msc.presentation.tabs.paging

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PositionalDataSource
import dev.olog.msc.core.coroutines.CustomScope
import dev.olog.msc.core.entity.ChunkRequest
import dev.olog.msc.core.gateway.BaseGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract internal class BaseDataSource<Model, PresentationModel> constructor(
    lifecycle: Lifecycle,
    gateway: BaseGateway<Model, *>
) : PositionalDataSource<PresentationModel>(), DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private val chunked = gateway.getChunk()

    init {
        launch(Dispatchers.Main) { lifecycle.addObserver(this@BaseDataSource) }
        launch {
            chunked.observeChanges()
                .collect {
                    cancel()
                    invalidate()
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<PresentationModel>) {
        val sizeDelta = headerSize()
        val count = chunked.allDataSize + sizeDelta
        val initialChunk =
            loadChunk(params.requestedStartPosition, params.requestedLoadSize - sizeDelta).toMutableList()
        val result = enrichInitialData(initialChunk)
        callback.onResult(result, 0, count)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<PresentationModel>) {
        callback.onResult(loadChunk(params.startPosition, params.loadSize))
    }

    private fun loadChunk(offset: Int, limit: Int): List<PresentationModel> {
        return chunked.chunkOf(ChunkRequest(offset = offset, limit = limit)).map { mapData(it) }

    }

    protected open fun headerSize() = 0

    protected abstract fun enrichInitialData(original: List<PresentationModel>): List<PresentationModel>

    protected abstract fun mapData(original: Model): PresentationModel

}