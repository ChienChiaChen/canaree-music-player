package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.R
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import javax.inject.Inject

class MiniPlayerTheme @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val prefs: SharedPreferences
) : DefaultLifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

    private var THEME = MiniPlayerThemeEnum.OPAQUE

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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key){
            context.getString(R.string.prefs_mini_player_appearance_key) -> {
                val value = context.getString(R.string.prefs_mini_player_appearance_key)
                val default = context.getString(R.string.prefs_mini_player_appearance_entry_value_opaque)
                onThemeChanged(prefs.getString(value, default)!!)
            }
        }
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
                context.getString(R.string.prefs_mini_player_appearance_key),
                context.getString(R.string.prefs_mini_player_appearance_entry_value_opaque)
        )!!
        onThemeChanged(initialTheme)
    }

    private fun onThemeChanged(theme: String) {
        THEME = when (theme){
            context.getString(R.string.prefs_mini_player_appearance_entry_value_opaque) -> MiniPlayerThemeEnum.OPAQUE
            context.getString(R.string.prefs_mini_player_appearance_entry_value_blurry) -> MiniPlayerThemeEnum.BLURRY
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    fun isOpaque() : Boolean {
        return THEME == MiniPlayerThemeEnum.OPAQUE
    }

    fun isBlurry() : Boolean {
        return THEME == MiniPlayerThemeEnum.BLURRY
    }

}