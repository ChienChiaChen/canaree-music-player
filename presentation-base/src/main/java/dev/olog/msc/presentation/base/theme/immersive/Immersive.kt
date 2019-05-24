package dev.olog.msc.presentation.base.theme.immersive

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.base.R
import io.reactivex.BackpressureStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

class Immersive @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences,
    private val rxPrefs: RxSharedPreferences
) : DefaultLifecycleObserver, IImmersive {

    private var job: Job? = null
    private var isEnabled = false

    private var currentActivity: Activity? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        setInitialValue()
        job?.cancel()
        job = GlobalScope.launch {
            rxPrefs.getBoolean(
                context.getString(R.string.prefs_immersive_key),
                false
            )
                .asObservable()
                .toFlowable(BackpressureStrategy.LATEST)
                .asFlow()
                .drop(1) // skip initial emission
                .collect {
                    withContext(Dispatchers.Main) {
                        onThemeChanged(it)
                        currentActivity?.recreate()
                    }
                }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
    }

    private fun setInitialValue() {
        val initialValue = prefs.getBoolean(
            context.getString(R.string.prefs_immersive_key),
            false
        )
        onThemeChanged(initialValue)
    }

    private fun onThemeChanged(enabled: Boolean) {
        isEnabled = enabled
    }

    override fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    override fun isEnabled(): Boolean = isEnabled
}

