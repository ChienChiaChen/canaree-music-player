package dev.olog.msc.app.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dev.olog.msc.shared.ui.theme.HasImmersive
import dev.olog.msc.shared.ui.theme.HasMiniPlayerTheme
import dev.olog.msc.shared.ui.theme.HasPlayerTheme
import dev.olog.msc.theme.DarkMode
import dev.olog.msc.theme.Immersive
import dev.olog.msc.theme.MiniPlayerTheme
import dev.olog.msc.theme.PlayerTheme
import javax.inject.Inject

abstract class ThemedApp : BaseApp(),
        HasPlayerTheme,
        HasImmersive,
        HasMiniPlayerTheme,
        Application.ActivityLifecycleCallbacks {

    @Suppress("unused")
    @Inject
    lateinit var darkMode: DarkMode
    @Inject
    lateinit var playerTheme: PlayerTheme

    @Inject
    lateinit var miniPlayerTheme: MiniPlayerTheme

    @Inject
    lateinit var immersive: Immersive

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

    override fun isOpaque(): Boolean {
        return miniPlayerTheme.isOpaque()
    }

    override fun isBlurry(): Boolean {
        return miniPlayerTheme.isBlurry()
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