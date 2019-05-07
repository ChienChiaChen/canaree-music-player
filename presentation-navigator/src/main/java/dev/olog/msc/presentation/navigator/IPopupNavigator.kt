package dev.olog.msc.presentation.navigator

import androidx.fragment.app.FragmentActivity

interface IPopupNavigator {
    fun toAboutActivity(activity: FragmentActivity)
    fun toEqualizer(activity: FragmentActivity)
    fun toDebugConfiguration(activity: FragmentActivity)
    fun toSettingsActivity(activity: FragmentActivity)
    fun toSleepTimer(activity: FragmentActivity)
}