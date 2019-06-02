package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.base.R
import javax.inject.Inject

class DarkMode @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences
) : DefaultLifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

    private var hasInit: Boolean = false

    init {
        lifecycle.addObserver(this)
        tryInitialize()
    }

    override fun onStart(owner: LifecycleOwner) {
        tryInitialize()
    }

    private fun tryInitialize(){
        if (!hasInit){
            prefs.registerOnSharedPreferenceChangeListener(this)
            setInitialValue()
            hasInit = true
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key){
            context.getString(R.string.prefs_dark_mode_2_key) -> onThemeChanged(prefs.getString(key, context.getString(R.string.prefs_dark_mode_2_value_follow_system))!!)
        }
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
            context.getString(R.string.prefs_dark_mode_2_value_follow_system) -> {
                if (BuildCompat.isAtLeastQ()){
                    // TODO update when Q will be releases
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            context.getString(R.string.prefs_dark_mode_2_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.prefs_dark_mode_2_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw IllegalStateException("invalid theme=$theme")
        }
        AppCompatDelegate.setDefaultNightMode(darkMode)
    }

}

