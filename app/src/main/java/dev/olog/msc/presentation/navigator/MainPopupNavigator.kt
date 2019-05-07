package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.debug.DebugConfigurationActivity
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.msc.shared.extensions.toast
import dev.olog.presentation.base.ActivityCodes
import dev.olog.presentation.base.interfaces.HasBilling
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
        private val prefsKeys: PrefsKeys
) : IPopupNavigator {

    override fun toAboutActivity(activity: FragmentActivity) {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    override fun toEqualizer(activity: FragmentActivity) {
        // TODO
        val useAppEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                .getBoolean(activity.getString(prefsKeys.usedEqualizer()), true)

        if (activity is HasBilling && activity.billing.isPremium() && useAppEqualizer) {
            toBuiltInEqualizer(activity)
        } else {
            searchForEqualizer(activity)
        }
    }

    private fun toBuiltInEqualizer(activity: FragmentActivity) {
        val instance = EqualizerFragment.newInstance()
        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
    }

    private fun searchForEqualizer(activity: FragmentActivity) {
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            activity.toast("Equalizer not found")
        }
    }

    override fun toDebugConfiguration(activity: FragmentActivity) {
        val intent = Intent(activity, DebugConfigurationActivity::class.java)
        activity.startActivity(intent)
    }

    override fun toSettingsActivity(activity: FragmentActivity) {
        val intent = Intent(activity, PreferencesActivity::class.java)
        activity.startActivityForResult(intent, ActivityCodes.REQUEST_CODE)
    }

    override fun toSleepTimer(activity: FragmentActivity) {
        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager)
                .show()
    }

}