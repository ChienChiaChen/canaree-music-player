package dev.olog.msc.app.injection.navigator

import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.navigator.IPopupNavigator
import javax.inject.Inject

class MainPopupNavigator @Inject constructor() : IPopupNavigator {

//    override fun toAboutActivity(activity: FragmentActivity) {
//        val intent = Intent(activity, AboutActivity::class.java)
//        activity.startActivity(intent)
//    }
//
//    override fun toEqualizer(activity: FragmentActivity) {
//        // TODO
//        val useAppEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
//            .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)
//
//        if (activity is HasBilling && activity.billing.isPremium() && useAppEqualizer) {
//            toBuiltInEqualizer(activity)
//        } else {
//            searchForEqualizer(activity)
//        }
//    }
//
//    private fun toBuiltInEqualizer(activity: FragmentActivity) {
//        val instance = EqualizerFragment.newInstance()
//        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
//    }
//
//    private fun searchForEqualizer(activity: FragmentActivity) {
//        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
//        if (intent.resolveActivity(activity.packageManager) != null) {
//            activity.startActivity(intent)
//        } else {
//            activity.toast("Equalizer not found")
//        }
//    }
//
//    override fun toDebugConfiguration(activity: FragmentActivity) {
//        // TODO
////        val intent = Intent(activity, DebugConfigurationActivity::class.java)
////        activity.startActivity(intent)
//    }
//
//    override fun toSettingsActivity(activity: FragmentActivity) {
//        val intent = Intent(activity, PreferencesActivity::class.java)
//        activity.startActivity(intent)
//    }
//
//    override fun toSleepTimer(activity: FragmentActivity) {
//        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager)
//            .show()
//    }
//
//    override fun toCreatePlaylistDialog(
//        activity: FragmentActivity,
//        mediaId: MediaId,
//        listSize: Int,
//        itemTitle: String
//    ) {
//        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
//    }

    override fun toAboutActivity(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toEqualizer(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toDebugConfiguration(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toSettingsActivity(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toSleepTimer(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}