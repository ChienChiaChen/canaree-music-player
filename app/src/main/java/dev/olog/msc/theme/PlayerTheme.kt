package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.base.R
import javax.inject.Inject

class PlayerTheme @Inject constructor(
    @ApplicationContext private val context: Context,
    @ProcessLifecycle lifecycle: Lifecycle,
    private val prefs: SharedPreferences
) : DefaultLifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

    private var THEME = PlayerThemeEnum.DEFAULT

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
            context.getString(R.string.prefs_appearance_key) -> {
                val value = context.getString(R.string.prefs_appearance_key)
                val default = context.getString(R.string.prefs_appearance_entry_value_default)
                onThemeChanged(prefs.getString(value, default)!!)
            }
        }
    }

    private fun setInitialValue() {
        val initialTheme = prefs.getString(
            context.getString(R.string.prefs_appearance_key),
            context.getString(R.string.prefs_appearance_entry_value_default)
        )!!
        onThemeChanged(initialTheme)
    }

    private fun onThemeChanged(theme: String) {
        THEME = when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> PlayerThemeEnum.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> PlayerThemeEnum.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> PlayerThemeEnum.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> PlayerThemeEnum.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> PlayerThemeEnum.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> PlayerThemeEnum.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> PlayerThemeEnum.MINI
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    fun isDefault(): Boolean {
        return THEME == PlayerThemeEnum.DEFAULT
    }

    fun isFlat(): Boolean {
        return THEME == PlayerThemeEnum.FLAT
    }

    fun isSpotify(): Boolean {
        return THEME == PlayerThemeEnum.SPOTIFY
    }

    fun isFullscreen(): Boolean {
        return THEME == PlayerThemeEnum.FULLSCREEN
    }

    fun isBigImage(): Boolean {
        return THEME == PlayerThemeEnum.BIG_IMAGE
    }

    fun isClean(): Boolean {
        return THEME == PlayerThemeEnum.CLEAN
    }

    fun isMini(): Boolean {
        return THEME == PlayerThemeEnum.MINI
    }
}