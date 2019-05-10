package dev.olog.msc.app.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.olog.msc.presentation.base.theme.dark.mode.IDarkMode
import dev.olog.msc.presentation.base.theme.immersive.IImmersive
import dev.olog.msc.presentation.base.theme.player.theme.IPlayerTheme
import dev.olog.msc.shared.ui.theme.HasDarkMode
import dev.olog.msc.shared.ui.theme.HasImmersive
import dev.olog.msc.shared.ui.theme.HasPlayerTheme
import javax.inject.Inject

abstract class ThemedApp : BaseApp(),
    HasDarkMode,
    HasPlayerTheme,
    HasImmersive,
    Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var darkMode: IDarkMode
    @Inject
    lateinit var playerTheme: IPlayerTheme
    @Inject
    lateinit var immersive: IImmersive

    override fun isWhiteMode(): Boolean {
        return darkMode.isWhiteMode()
    }

    override fun isGrayMode(): Boolean {
        return darkMode.isGrayMode()
    }

    override fun isDarkMode(): Boolean {
        return darkMode.isDarkMode()
    }

    override fun isBlackMode(): Boolean {
        return darkMode.isBlackMode()
    }

    override fun onActivityStarted(activity: Activity?) {
        darkMode.setCurrentActivity(activity)
    }

    override fun onActivityStopped(activity: Activity?) {
        darkMode.setCurrentActivity(activity)
    }

    override fun isDefault(): Boolean {
        return playerTheme.isDefault()
    }

    override fun isFlat(): Boolean {
        return playerTheme.isFlat()
    }

    override fun isSpotify(): Boolean {
        return playerTheme.isSpotify()
    }

    override fun isFullscreen(): Boolean {
        return playerTheme.isFullscreen()
    }

    override fun isBigImage(): Boolean {
        return playerTheme.isBigImage()
    }

    override fun isClean(): Boolean {
        return playerTheme.isClean()
    }

    override fun isMini(): Boolean {
        return playerTheme.isMini()
    }

    override fun isEnabled(): Boolean {
        return immersive.isEnabled()
    }


    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }
}