package dev.olog.msc.presentation.base.activity

import android.content.Context
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.presentation.base.R
import dev.olog.msc.shared.ui.extensions.colorSecondary
import dev.olog.msc.shared.ui.theme.HasImmersive

interface ThemedActivity {

    fun themeAccentColor(context: Context, theme: Resources.Theme, prefsKeys: PrefsKeys){
        if ((context.applicationContext as HasImmersive).isEnabled()){
            theme.applyStyle(R.style.ThemeImmersive, true)
        }
        theme.applyStyle(getAccentStyle(context.applicationContext, prefsKeys), true)
    }

    private fun getAccentStyle(context: Context, prefsKeys: PrefsKeys): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val color = prefs.getInt(context.getString(prefsKeys.colorAccent()), context.colorSecondary())

        return when (color){
            ContextCompat.getColor(context, R.color.md_red_A100) -> R.style.ThemeAccentRed100
            ContextCompat.getColor(context, R.color.md_red_A200) -> R.style.ThemeAccentRed200
            ContextCompat.getColor(context, R.color.md_red_A400) -> R.style.ThemeAccentRed400
            ContextCompat.getColor(context, R.color.md_red_A700) -> R.style.ThemeAccentRed700
            ContextCompat.getColor(context, R.color.md_pink_A100) -> R.style.ThemeAccentPink100
            ContextCompat.getColor(context, R.color.md_pink_A200) -> R.style.ThemeAccentPink200
            ContextCompat.getColor(context, R.color.md_pink_A400) -> R.style.ThemeAccentPink400
            ContextCompat.getColor(context, R.color.md_pink_A700) -> R.style.ThemeAccentPink700
            ContextCompat.getColor(context, R.color.md_purple_A100) -> R.style.ThemeAccentPurple100
            ContextCompat.getColor(context, R.color.md_purple_A200) -> R.style.ThemeAccentPurple200
            ContextCompat.getColor(context, R.color.md_purple_A400) -> R.style.ThemeAccentPurple400
            ContextCompat.getColor(context, R.color.md_purple_A700) -> R.style.ThemeAccentPurple700
            ContextCompat.getColor(context, R.color.md_deep_purple_A100) -> R.style.ThemeAccentDeepPurple100
            ContextCompat.getColor(context, R.color.md_deep_purple_A200) -> R.style.ThemeAccentDeepPurple200
            ContextCompat.getColor(context, R.color.md_deep_purple_A400) -> R.style.ThemeAccentDeepPurple400
            ContextCompat.getColor(context, R.color.md_deep_purple_A700) -> R.style.ThemeAccentDeepPurple700
            ContextCompat.getColor(context, R.color.md_indigo_A100) -> R.style.ThemeAccentIndigo100
            ContextCompat.getColor(context, R.color.md_indigo_A200) -> R.style.ThemeAccentIndigo200
            ContextCompat.getColor(context, R.color.md_indigo_A400),
            ContextCompat.getColor(context, R.color.md_indigo_A400_alt) -> R.style.ThemeAccentIndigo400
            ContextCompat.getColor(context, R.color.md_indigo_A700) -> R.style.ThemeAccentIndigo700
            ContextCompat.getColor(context, R.color.md_blue_A100) -> R.style.ThemeAccentBlue100
            ContextCompat.getColor(context, R.color.md_blue_A200) -> R.style.ThemeAccentBlue200
            ContextCompat.getColor(context, R.color.md_blue_A400) -> R.style.ThemeAccentBlue400
            ContextCompat.getColor(context, R.color.md_blue_A700) -> R.style.ThemeAccentBlue700
            ContextCompat.getColor(context, R.color.md_light_blue_A100) -> R.style.ThemeAccentLightBlue100
            ContextCompat.getColor(context, R.color.md_light_blue_A200) -> R.style.ThemeAccentLightBlue200
            ContextCompat.getColor(context, R.color.md_light_blue_A400) -> R.style.ThemeAccentLightBlue400
            ContextCompat.getColor(context, R.color.md_light_blue_A700) -> R.style.ThemeAccentLightBlue700
            ContextCompat.getColor(context, R.color.md_cyan_A100) -> R.style.ThemeAccentCyan100
            ContextCompat.getColor(context, R.color.md_cyan_A200) -> R.style.ThemeAccentCyan200
            ContextCompat.getColor(context, R.color.md_cyan_A400) -> R.style.ThemeAccentCyan400
            ContextCompat.getColor(context, R.color.md_cyan_A700) -> R.style.ThemeAccentCyan700
            ContextCompat.getColor(context, R.color.md_teal_A100) -> R.style.ThemeAccentTeal100
            ContextCompat.getColor(context, R.color.md_teal_A200) -> R.style.ThemeAccentTeal200
            ContextCompat.getColor(context, R.color.md_teal_A400) -> R.style.ThemeAccentTeal400
            ContextCompat.getColor(context, R.color.md_teal_A700) -> R.style.ThemeAccentTeal700
            ContextCompat.getColor(context, R.color.md_green_A100) -> R.style.ThemeAccentGreen100
            ContextCompat.getColor(context, R.color.md_green_A200) -> R.style.ThemeAccentGreen200
            ContextCompat.getColor(context, R.color.md_green_A400) -> R.style.ThemeAccentGreen400
            ContextCompat.getColor(context, R.color.md_green_A700) -> R.style.ThemeAccentGreen700
            ContextCompat.getColor(context, R.color.md_light_green_A100) -> R.style.ThemeAccentLightGreen100
            ContextCompat.getColor(context, R.color.md_light_green_A200) -> R.style.ThemeAccentLightGreen200
            ContextCompat.getColor(context, R.color.md_light_green_A400) -> R.style.ThemeAccentLightGreen400
            ContextCompat.getColor(context, R.color.md_light_green_A700) -> R.style.ThemeAccentLightGreen700
            ContextCompat.getColor(context, R.color.md_lime_A100) -> R.style.ThemeAccentLime100
            ContextCompat.getColor(context, R.color.md_lime_A200) -> R.style.ThemeAccentLime200
            ContextCompat.getColor(context, R.color.md_lime_A400) -> R.style.ThemeAccentLime400
            ContextCompat.getColor(context, R.color.md_lime_A700) -> R.style.ThemeAccentLime700
            ContextCompat.getColor(context, R.color.md_yellow_A100) -> R.style.ThemeAccentYellow100
            ContextCompat.getColor(context, R.color.md_yellow_A200) -> R.style.ThemeAccentYellow200
            ContextCompat.getColor(context, R.color.md_yellow_A400) -> R.style.ThemeAccentYellow400
            ContextCompat.getColor(context, R.color.md_yellow_A700),
            ContextCompat.getColor(context, R.color.md_yellow_A700_alt) -> R.style.ThemeAccentYellow700
            ContextCompat.getColor(context, R.color.md_amber_A100) -> R.style.ThemeAccentAmber100
            ContextCompat.getColor(context, R.color.md_amber_A200) -> R.style.ThemeAccentAmber200
            ContextCompat.getColor(context, R.color.md_amber_A400) -> R.style.ThemeAccentAmber400
            ContextCompat.getColor(context, R.color.md_amber_A700) -> R.style.ThemeAccentAmber700
            ContextCompat.getColor(context, R.color.md_orange_A100) -> R.style.ThemeAccentOrange100
            ContextCompat.getColor(context, R.color.md_orange_A200) -> R.style.ThemeAccentOrange200
            ContextCompat.getColor(context, R.color.md_orange_A400) -> R.style.ThemeAccentOrange400
            ContextCompat.getColor(context, R.color.md_orange_A700) -> R.style.ThemeAccentOrange700
            ContextCompat.getColor(context, R.color.md_deep_orange_A100) -> R.style.ThemeAccentDeepOrange100
            ContextCompat.getColor(context, R.color.md_deep_orange_A200) -> R.style.ThemeAccentDeepOrange200
            ContextCompat.getColor(context, R.color.md_deep_orange_A400) -> R.style.ThemeAccentDeepOrange400
            ContextCompat.getColor(context, R.color.md_deep_orange_A700) -> R.style.ThemeAccentDeepOrange700
            // prevent strange color crash
            else -> R.style.ThemeAccentIndigo400 // must be in sync with R.attr.colorSecondary
        }
    }

}