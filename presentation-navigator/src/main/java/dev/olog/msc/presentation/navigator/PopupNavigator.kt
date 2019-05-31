package dev.olog.msc.presentation.navigator

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.core.MediaId
import javax.inject.Inject

class PopupNavigator @Inject constructor() {

    //    fun toAboutActivity(activity: FragmentActivity) {
//        val intent = Intent(activity, AboutActivity::class.java)
//        activity.startActivity(intent)
//    }
//
//    fun toEqualizer(activity: FragmentActivity) {
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
//    fun toDebugConfiguration(activity: FragmentActivity) {
//        // TODO
////        val intent = Intent(activity, DebugConfigurationActivity::class.java)
////        activity.startActivity(intent)
//    }
//
    fun toSettings(activity: FragmentActivity) {
        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.fragmentContainer, Fragments.settings(activity), Fragments.SETTINGS)
            addToBackStack(Fragments.SETTINGS)
        }
    }
//
//    fun toSleepTimer(activity: FragmentActivity) {
//        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager)
//            .show()
//    }
//
//    fun toCreatePlaylistDialog(
//        activity: FragmentActivity,
//        mediaId: MediaId,
//        listSize: Int,
//        itemTitle: String
//    ) {
//        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
//    }

    fun toAboutActivity(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toEqualizer(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toDebugConfiguration(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toSleepTimer(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}