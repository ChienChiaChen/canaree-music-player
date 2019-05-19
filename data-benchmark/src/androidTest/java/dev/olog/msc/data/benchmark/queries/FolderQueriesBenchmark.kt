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
import dev.olog.msc.data.repository.queries.FolderQueries
import dev.olog.msc.shared.TrackUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FolderQueriesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var folderQueries: FolderQueries

    private val requestPaged = Request(Page(0, 50), Filter.NO_FILTER)
    private val folderPath = "/storage/emulated/0"

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getTargetContext()
        TrackUtils.initialize("Unkwown artist", "Unkwown album")
        folderQueries = FolderQueries(AppPreferencesStub(), SortingStub(), appContext.contentResolver)
    }

    @Test
    fun benchmarkGetAll() {
        benchmarkRule.measureRepeated {
            val cursor = folderQueries.getAll(requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkGetSongList() {
        benchmarkRule.measureRepeated {
            val cursor = folderQueries.getSongList(folderPath, requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkSiblings() {
        benchmarkRule.measureRepeated {
            val cursor = folderQueries.getSiblings(folderPath, requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkRelatedArtists() {
        benchmarkRule.measureRepeated {
            val cursor = folderQueries.getRelatedArtists(folderPath, requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

    @Test
    fun benchmarkRecentlyAdded() {
        benchmarkRule.measureRepeated {
            val cursor = folderQueries.getRecentlyAddedSongs(folderPath, requestPaged)
            runWithTimingDisabled {
                cursor.close()
            }
        }
    }

}