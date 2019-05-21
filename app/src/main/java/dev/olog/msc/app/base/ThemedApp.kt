package dev.olog.msc.app.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.olog.msc.presentation.base.theme.dark.mode.IDarkMode
import dev.olog.msc.presentation.base.theme.immersive.IImmersive
import dev.olog.msc.presentation.base.theme.player.theme.IPlayerTheme
import dev.olog.msc.shared.ui.theme.HasImmersive
import dev.olog.msc.shared.ui.theme.HasPlayerTheme
import javax.inject.Inject

abstract class ThemedApp : BaseApp(),
        HasPlayerTheme,
        HasImmersive,
        Application.ActivityLifecycleCallbacks {

    @Suppress("unused")
    @Inject
    lateinit var darkMode: IDarkMode
    @Inject
    lateinit var playerTheme: IPlayerTheme
    @Inject
    lateinit var immersive: IImmersive

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
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