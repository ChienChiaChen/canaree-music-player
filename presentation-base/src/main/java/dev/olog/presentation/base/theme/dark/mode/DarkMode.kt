package dev.olog.presentation.base.theme.dark.mode

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.presentation.base.R
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DarkMode @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences,
    rxPrefs: RxSharedPreferences
) : DefaultLifecycleObserver, IDarkMode {

    private var disposable: Disposable? = null
    private var DARK_MODE = DarkModeEnum.NONE

    private var currentActivity: Activity? = null

    init {
        lifecycle.addObserver(this)
        setInitialValue()
        disposable = rxPrefs.getString(
            context.getString(R.string.prefs_dark_mode_key),
            context.getString(R.string.prefs_dark_mode_entry_value_white)
        )
            .asObservable()
            .subscribeOn(Schedulers.io())
            .skip(1) // skip initial emission
            .subscribe({
                onThemeChanged(it)
                currentActivity?.recreate()
            }, Throwable::printStackTrace)
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
            context.getString(R.string.prefs_dark_mode_key),
            context.getString(R.string.prefs_dark_mode_entry_value_white)
        )
        onThemeChanged(initialTheme)
    }

    private fun onThemeChanged(theme: String) {
        DARK_MODE = when (theme) {
            context.getString(dev.olog.msc.shared.ui.R.string.prefs_dark_mode_entry_value_white) -> DarkModeEnum.NONE
            context.getString(dev.olog.msc.shared.ui.R.string.prefs_dark_mode_entry_value_gray) -> DarkModeEnum.LIGHT
            context.getString(dev.olog.msc.shared.ui.R.string.prefs_dark_mode_entry_value_dark) -> DarkModeEnum.DARK
            context.getString(dev.olog.msc.shared.ui.R.string.prefs_dark_mode_entry_value_black) -> DarkModeEnum.BLACK
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    override fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    override fun isWhiteMode(): Boolean {
        return DARK_MODE == DarkModeEnum.NONE
    }

    override fun isGrayMode(): Boolean {
        return DARK_MODE == DarkModeEnum.LIGHT
    }

    override fun isDarkMode(): Boolean {
        return DARK_MODE == DarkModeEnum.DARK
    }

    override fun isBlackMode(): Boolean {
        return DARK_MODE == DarkModeEnum.BLACK
    }
}

