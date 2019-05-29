package dev.olog.msc.app.injection.navigator

import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId

interface IPopupNavigator {
    fun toAboutActivity(activity: FragmentActivity)
    fun toEqualizer(activity: FragmentActivity)
    fun toDebugConfiguration(activity: FragmentActivity)
    fun toSettingsActivity(activity: FragmentActivity)
    fun toSleepTimer(activity: FragmentActivity)
    fun toCreatePlaylistDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

}