package dev.olog.msc.presentation.base.paging

import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PositionalDataSource
import dev.olog.msc.core.coroutines.CustomScope
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.math.abs

/**
 * Offsets the page offset by header size, and load footers when scrolling to end of the list
 */
abstract class BaseDataSource<PresentationModel> :
    PositionalDataSource<PresentationModel>(),
    DefaultLifecycleObserver,
    CoroutineScope by CustomScope() {

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
    }

    private val headers = mutableListOf<PresentationModel>()
    private var footers = listOf<PresentationModel>()

    protected abstract fun getMainDataSize(): Int
    protected abstract fun getHeaders(mainListSize: Int): List<PresentationModel>
    protected abstract fun getFooters(mainListSize: Int): List<PresentationModel>

    @CallSuper
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<PresentationModel>) {
        if (canLoadData) {
            val mainDataSize = getMainDataSize()

            headers.addAll(getHeaders(mainDataSize))
            footers = getFooters(mainDataSize)
            val footerSize = footers.size

            val result = mutableListOf<PresentationModel>()
            result.addAll(headers)
            result.addAll(
                loadInternal(
                    Request(
                        Page(params.requestedStartPosition, params.requestedLoadSize - headers.size),
                        Filter.NO_FILTER
                    )
                )
            )
            tryAddFooter(result, params.requestedLoadSize)
            callback.onResult(result, 0, mainDataSize + headers.size + footerSize)
        }
    }

    @CallSuper
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<PresentationModel>) {
        if (canLoadData) {
            val result = mutableListOf<PresentationModel>()
            result.addAll(
                loadInternal(
                    Request(
                        Page(params.startPosition - headers.size, params.loadSize),
                        Filter.NO_FILTER
                    )
                )
            )
            tryAddFooter(result, params.loadSize)

            callback.onResult(result)
        }
    }

    private fun tryAddFooter(toList: MutableList<PresentationModel>, pageSize: Int) {
        if (toList.size != pageSize && footers.isNotEmpty()) {
            val diff = abs(toList.size - pageSize)
            toList.addAll(footers.take(diff))
            footers = footers.drop(diff)
        }
    }

    protected abstract fun loadInternal(request: Request): List<PresentationModel>

    protected open val canLoadData = true

}