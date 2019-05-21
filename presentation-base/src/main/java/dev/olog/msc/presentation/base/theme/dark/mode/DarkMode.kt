package dev.olog.msc.presentation.base.theme.dark.mode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
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

class DarkMode @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val prefs: SharedPreferences,
        private val rxPrefs: RxSharedPreferences
) : DefaultLifecycleObserver, IDarkMode {

    private var disposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        setInitialValue()
        disposable = rxPrefs.getString(
                context.getString(R.string.prefs_dark_mode_2_key),
                context.getString(R.string.prefs_dark_mode_2_value_follow_system)
        )
                .asObservable()
                .subscribeOn(Schedulers.io())
                .skip(1) // skip initial emission
                .subscribe({
                    onThemeChanged(it)
                }, Throwable::printStackTrace)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
                context.getString(R.string.prefs_dark_mode_2_key),
                context.getString(R.string.prefs_dark_mode_2_value_follow_system)
        )
        onThemeChanged(initialTheme)
    }

    private fun onThemeChanged(theme: String) {
        val darkMode = when (theme) {
            context.getString(R.string.prefs_dark_mode_2_value_follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            context.getString(R.string.prefs_dark_mode_2_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$theme")
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

}

