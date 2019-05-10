package dev.olog.msc.presentation.home

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.presentation.base.extensions.removeLightStatusBar
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.presentation.base.interfaces.CanChangeStatusBarColor
import dev.olog.msc.presentation.base.theme.player.theme.isBigImage
import dev.olog.msc.presentation.base.theme.player.theme.isFullscreen
import dev.olog.msc.shared.extensions.isPortrait
import dev.olog.msc.shared.utils.isMarshmallow
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class StatusBarColorBehavior @Inject constructor(
    private val activity: MainActivity

) : DefaultLifecycleObserver,
        SlidingUpPanelLayout.PanelSlideListener,
        androidx.fragment.app.FragmentManager.OnBackStackChangedListener {

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!isMarshmallow()){
            return
        }

        activity.addPanelSlideListener(this)
        activity.supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        if (!isMarshmallow()){
            return
        }

        activity.removePanelSlideListener(this)
        activity.supportFragmentManager.removeOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        if (!isMarshmallow()){
            return
        }

        val fragment = searchForDetailFragmentOnPortraitMode()
        if (fragment == null){
            activity.window.setLightStatusBar()
        } else {
            if (activity.slidingPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED){
                activity.window.setLightStatusBar()
            } else {
                fragment.adjustStatusBarColor()
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
        if (!isMarshmallow()){
            return
        }
        val context = panel.context

        when (newState){
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                if (context.isFullscreen() || context.isBigImage()){
                    activity.window.removeLightStatusBar()
                } else {
                    activity.window.setLightStatusBar()
                }
            }
            SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                searchForDetailFragmentOnPortraitMode()?.adjustStatusBarColor()
                    ?: activity.window.setLightStatusBar()
            }
        }
    }

    private fun searchForDetailFragmentOnPortraitMode(): CanChangeStatusBarColor? {
        if (activity.isPortrait){
            val fm = activity.supportFragmentManager
            val backStackEntryCount = fm.backStackEntryCount - 1
            if (backStackEntryCount > -1){
                val entry = fm.getBackStackEntryAt(backStackEntryCount)
                val fragment = fm.findFragmentByTag(entry.name)
                if (fragment is CanChangeStatusBarColor) {
                    return fragment
                }
            }
        }
        return null
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

}