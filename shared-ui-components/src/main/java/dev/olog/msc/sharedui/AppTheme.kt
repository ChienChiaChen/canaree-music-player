package dev.olog.msc.sharedui

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager

object AppTheme {

    enum class Theme {
        DEFAULT, FLAT, SPOTIFY, FULLSCREEN, BIG_IMAGE, CLEAN, MINI;
    }

    enum class DarkMode {
        NONE, LIGHT, DARK, BLACK
    }

    enum class Immersive {
        DISABLED, ENABLED
    }

    private var THEME = AppTheme.Theme.DEFAULT
    private var DARK_MODE = AppTheme.DarkMode.NONE
    private var IMMERSIVE_MDOE = AppTheme.Immersive.DISABLED

    fun initialize(app: Application){
        updateTheme(app)
        updateDarkMode(app)
        updateImmersive(app)
    }

    fun isImmersiveMode(): Boolean = IMMERSIVE_MDOE == AppTheme.Immersive.ENABLED

    fun isDefaultTheme(): Boolean = THEME == AppTheme.Theme.DEFAULT
    fun isFlatTheme(): Boolean = THEME == AppTheme.Theme.FLAT
    fun isSpotifyTheme(): Boolean = THEME == AppTheme.Theme.SPOTIFY
    fun isFullscreenTheme(): Boolean = THEME == AppTheme.Theme.FULLSCREEN
    fun isBigImageTheme(): Boolean = THEME == AppTheme.Theme.BIG_IMAGE
    fun isCleanTheme(): Boolean = THEME == AppTheme.Theme.CLEAN
    fun isMiniTheme(): Boolean = THEME == AppTheme.Theme.MINI

    fun isWhiteMode(): Boolean = DARK_MODE == AppTheme.DarkMode.NONE
    fun isGrayMode(): Boolean = DARK_MODE == AppTheme.DarkMode.LIGHT
    fun isDarkMode(): Boolean = DARK_MODE == AppTheme.DarkMode.DARK
    fun isBlackMode(): Boolean = DARK_MODE == AppTheme.DarkMode.BLACK

    fun isWhiteTheme(): Boolean = DARK_MODE == AppTheme.DarkMode.NONE || DARK_MODE == AppTheme.DarkMode.LIGHT
    fun isDarkTheme(): Boolean = DARK_MODE == AppTheme.DarkMode.DARK || DARK_MODE == AppTheme.DarkMode.BLACK

    fun updateTheme(context: Context){
        THEME = getTheme(context)
    }

    fun updateDarkMode(context: Context){
        DARK_MODE = getDarkMode(context)
    }

    fun updateImmersive(context: Context){
        IMMERSIVE_MDOE = getImmersiveMode(context)
    }

    private fun getImmersiveMode(context: Context): Immersive {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val isImmersive = prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false)
        return when {
            isImmersive -> AppTheme.Immersive.ENABLED
            else -> AppTheme.Immersive.DISABLED
        }
    }

    private fun getTheme(context: Context): Theme {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
        return when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> AppTheme.Theme.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> AppTheme.Theme.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> AppTheme.Theme.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> AppTheme.Theme.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> AppTheme.Theme.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> AppTheme.Theme.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> AppTheme.Theme.MINI
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    private fun getDarkMode(context: Context): DarkMode {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_dark_mode_key), context.getString(R.string.prefs_dark_mode_entry_value_white))
        return when (theme) {
            context.getString(R.string.prefs_dark_mode_entry_value_white) -> AppTheme.DarkMode.NONE
            context.getString(R.string.prefs_dark_mode_entry_value_gray) -> AppTheme.DarkMode.LIGHT
            context.getString(R.string.prefs_dark_mode_entry_value_dark) -> AppTheme.DarkMode.DARK
            context.getString(R.string.prefs_dark_mode_entry_value_black) -> AppTheme.DarkMode.BLACK
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

}