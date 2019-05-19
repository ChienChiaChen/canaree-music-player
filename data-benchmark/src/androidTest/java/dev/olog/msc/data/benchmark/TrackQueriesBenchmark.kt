package dev.olog.msc.data.benchmark

import android.util.Log
import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.test.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class TrackQueriesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun log() {
        benchmarkRule.measureRepeated {
            Log.d("LogBenchmark", "the cost of writing this log method will be measured")
        }
    }

    @Test
    fun benchmark(){
        val appContext = InstrumentationRegistry.getTargetContext()
        val contentResolver = appContext.contentResolver

        benchmarkRule.measureRepeated {

        }
    }


}