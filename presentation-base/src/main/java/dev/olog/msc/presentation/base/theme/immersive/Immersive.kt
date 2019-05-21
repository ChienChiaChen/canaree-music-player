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
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class Immersive @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val prefs: SharedPreferences,
        private val rxPrefs: RxSharedPreferences
) : DefaultLifecycleObserver, IImmersive {

    private var disposable: Disposable? = null
    private var isEnabled = false

    private var currentActivity: Activity? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        setInitialValue()
        disposable = rxPrefs.getBoolean(
                context.getString(R.string.prefs_immersive_key),
                false
        )
                .asObservable()
                .subscribeOn(Schedulers.io())
                .skip(1) // skip initial emission
                .subscribe({
                    onThemeChanged(it)
                    currentActivity?.recreate()
                }, Throwable::printStackTrace)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
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

