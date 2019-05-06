package dev.olog.msc.shared.ui.theme

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.shared.ui.R

object AppTheme {

    private var THEME = PlayerTheme.DEFAULT
    private var DARK_MODE = DarkMode.NONE
    private var IMMERSIVE_MDOE = ImmersiveMode.DISABLED

    fun initialize(app: Application){
        updateTheme(app)
        updateDarkMode(app)
        updateImmersive(app)
    }

    fun isImmersiveMode(): Boolean = IMMERSIVE_MDOE == ImmersiveMode.ENABLED

    fun isDefaultTheme(): Boolean = THEME == PlayerTheme.DEFAULT
    fun isFlatTheme(): Boolean = THEME == PlayerTheme.FLAT
    fun isSpotifyTheme(): Boolean = THEME == PlayerTheme.SPOTIFY
    fun isFullscreenTheme(): Boolean = THEME == PlayerTheme.FULLSCREEN
    fun isBigImageTheme(): Boolean = THEME == PlayerTheme.BIG_IMAGE
    fun isCleanTheme(): Boolean = THEME == PlayerTheme.CLEAN
    fun isMiniTheme(): Boolean = THEME == PlayerTheme.MINI

    fun isWhiteMode(): Boolean = DARK_MODE == DarkMode.NONE
    fun isGrayMode(): Boolean = DARK_MODE == DarkMode.LIGHT
    fun isDarkMode(): Boolean = DARK_MODE == DarkMode.DARK
    fun isBlackMode(): Boolean = DARK_MODE == DarkMode.BLACK

    fun isWhiteTheme(): Boolean = DARK_MODE == DarkMode.NONE || DARK_MODE == DarkMode.LIGHT
    fun isDarkTheme(): Boolean = DARK_MODE == DarkMode.DARK || DARK_MODE == DarkMode.BLACK

    fun updateTheme(context: Context){
        THEME = getTheme(context)
    }

    fun updateDarkMode(context: Context){
        DARK_MODE = getDarkMode(context)
    }

    fun updateImmersive(context: Context){
        IMMERSIVE_MDOE = getImmersiveMode(context)
    }

    private fun getImmersiveMode(context: Context): ImmersiveMode {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val isImmersive = prefs.getBoolean(context.getString(R.string.prefs_immersive_key), false)
        return when {
            isImmersive -> ImmersiveMode.ENABLED
            else -> ImmersiveMode.DISABLED
        }
    }

    private fun getTheme(context: Context): PlayerTheme {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_appearance_key), context.getString(R.string.prefs_appearance_entry_value_default))
        return when (theme) {
            context.getString(R.string.prefs_appearance_entry_value_default) -> PlayerTheme.DEFAULT
            context.getString(R.string.prefs_appearance_entry_value_flat) -> PlayerTheme.FLAT
            context.getString(R.string.prefs_appearance_entry_value_spotify) -> PlayerTheme.SPOTIFY
            context.getString(R.string.prefs_appearance_entry_value_fullscreen) -> PlayerTheme.FULLSCREEN
            context.getString(R.string.prefs_appearance_entry_value_big_image) -> PlayerTheme.BIG_IMAGE
            context.getString(R.string.prefs_appearance_entry_value_clean) -> PlayerTheme.CLEAN
            context.getString(R.string.prefs_appearance_entry_value_mini) -> PlayerTheme.MINI
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

    private fun getDarkMode(context: Context): DarkMode {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val theme = prefs.getString(context.getString(R.string.prefs_dark_mode_key), context.getString(R.string.prefs_dark_mode_entry_value_white))
        return when (theme) {
            context.getString(R.string.prefs_dark_mode_entry_value_white) -> DarkMode.NONE
            context.getString(R.string.prefs_dark_mode_entry_value_gray) -> DarkMode.LIGHT
            context.getString(R.string.prefs_dark_mode_entry_value_dark) -> DarkMode.DARK
            context.getString(R.string.prefs_dark_mode_entry_value_black) -> DarkMode.BLACK
            else -> throw IllegalStateException("invalid theme=$theme")
        }
    }

}