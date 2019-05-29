package dev.olog.msc.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.base.R
import javax.inject.Inject

class Immersive @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences
) : DefaultLifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

    private var isEnabled = false

    private var currentActivity: Activity? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        prefs.registerOnSharedPreferenceChangeListener(this)
        setInitialValue()
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key){
            context.getString(R.string.prefs_immersive_key) -> {
                onThemeChanged(prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false))
                currentActivity?.recreate()
            }
        }
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

    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    fun isEnabled(): Boolean = isEnabled
}

