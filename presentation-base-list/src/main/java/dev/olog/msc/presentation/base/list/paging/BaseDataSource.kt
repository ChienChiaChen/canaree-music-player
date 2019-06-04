package dev.olog.msc.presentation.base.list.paging

import androidx.annotation.CallSuper
import androidx.core.math.MathUtils
import androidx.paging.PositionalDataSource
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.shared.core.coroutines.CustomScope
import kotlinx.coroutines.*
import kotlin.math.abs

/**
 * Offsets the page offset by header size, and load footers when scrolling to end of the list
 */
abstract class BaseDataSource<PresentationModel> :
    PositionalDataSource<PresentationModel>(),
    CoroutineScope by CustomScope() {

    abstract fun onAttach()

    fun onDetach() {
        cancel()
    }

    override fun invalidate() {
        onDetach()
        super.invalidate()
    }


    private val headers = mutableListOf<PresentationModel>()
    private var footers = listOf<PresentationModel>()

    protected abstract suspend fun getMainDataSize(): Int
    protected abstract suspend fun getHeaders(mainListSize: Int): List<PresentationModel>
    protected abstract suspend fun getFooters(mainListSize: Int): List<PresentationModel>

    @CallSuper
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<PresentationModel>) = runBlocking {
        if (!canLoadData){
            return@runBlocking
        }

        val mainDataSize = getMainDataSize()

        val safeStartPosition = MathUtils.clamp(
            params.requestedStartPosition,
            0,
            mainDataSize
        )
        val safeLoadSize = MathUtils.clamp(
            params.requestedLoadSize /*- headers.size*/,
            0,
            mainDataSize
        )

        launch {
            // Parallel call, get headers, footer and data all in parallel.
            val asyncData = listOf(
                async { getHeaders(mainDataSize) },
                async { getFooters(mainDataSize) },
                // (*) since 'headers' are not available yet, it's mandatory to clamp the result
                async { loadInternal(Request(Page(safeStartPosition, safeLoadSize), Filter.NO_FILTER)) }
            ).awaitAll()

            headers.addAll(asyncData[0])
            footers = asyncData[1]
            val headersSize = headers.size
            val footerSize = footers.size

            // (*) clamping result
            val result = headers.plus(asyncData[2])
                .take(params.requestedLoadSize)
                .toMutableList()
            // adds footer is resulting list size is minor than the page request
            tryAddFooter(result, params.requestedLoadSize)

            callback.onResult(result, 0, headersSize + mainDataSize + footerSize)

        }.join()
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

    protected suspend fun loadParallel(vararg deferred: Deferred<List<PresentationModel>>): List<PresentationModel>{
        val result = mutableListOf<PresentationModel>()
        for (list in deferred.toList().awaitAll()) {
            result.addAll(list)
        }
        return result
    }

    protected open val canLoadData = true

}