package dev.olog.msc.data.benchmark.queries

import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.test.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.data.benchmark.stub.AppPreferencesStub
import dev.olog.msc.data.benchmark.stub.SortingStub
import dev.olog.msc.data.repository.queries.TrackQueries
import dev.olog.msc.shared.TrackUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackQueriesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var trackQueries: TrackQueries

    private val requestPaged = Request(Page(0, 50), Filter.NO_FILTER)
    private val requestPagedWithFilter = Request(
        Page(0, 50),
        Filter("test", arrayOf(Filter.By.TITLE, Filter.By.ARTIST, Filter.By.ALBUM))
    )
    private val requestAll = Request(Page.NO_PAGING, Filter.NO_FILTER)

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getTargetContext()
        TrackUtils.initialize("Unkwown artist", "Unkwown album")
        trackQueries = TrackQueries(SortingStub(), AppPreferencesStub(), false, appContext.contentResolver)
    }

    @Test
    fun benchmarkGetAll() {
        benchmarkRule.measureRepeated {
            val cursor = trackQueries.getAll(requestAll)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkGetAllPaged() {
        benchmarkRule.measureRepeated {
            val cursor = trackQueries.getAll(requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkGetAllPagedWithFilter() {
        benchmarkRule.measureRepeated {
            val cursor = trackQueries.getAll(requestPagedWithFilter)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }


}