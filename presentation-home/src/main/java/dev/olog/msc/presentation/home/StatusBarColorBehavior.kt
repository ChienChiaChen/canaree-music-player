package dev.olog.msc.presentation.home

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.presentation.base.extensions.isExpanded
import dev.olog.msc.presentation.base.extensions.removeLightStatusBar
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.presentation.base.interfaces.CanChangeStatusBarColor
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import dev.olog.msc.shared.ui.theme.playerTheme
import dev.olog.msc.shared.utils.isMarshmallow
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Handle status bar color on SDK >= 23 when:
 * 1) Sliding panel changes state
 * 2) Entering a CanChangeStatusBarColor fragment (probably only DetailFragment)
 */
class StatusBarColorBehavior @Inject constructor(
        private val activity: AppCompatActivity

) : DefaultLifecycleObserver,
        SlidingUpPanelLayout.PanelSlideListener,
        FragmentManager.OnBackStackChangedListener {

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!isMarshmallow()) {
            return
        }

        if (activity is HasSlidingPanel){
            activity.addPanelSlideListener(this)
        }
        activity.supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        if (!isMarshmallow()) {
            return
        }

        if (activity is HasSlidingPanel){
            activity.removePanelSlideListener(this)
        }
        activity.supportFragmentManager.removeOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        if (!isMarshmallow()) {
            return
        }

        val fragment = searchForDetailFragment()
        if (fragment == null) {
            activity.window.setLightStatusBar()
        } else {
            if (activity.slidingPanel.isExpanded()) {
                activity.window.setLightStatusBar()
            } else {
                fragment.adjustStatusBarColor()
            }
        }
    }

    private fun searchForDetailFragment(): CanChangeStatusBarColor? {
        val fm = activity.supportFragmentManager
        val backStackEntryCount = fm.backStackEntryCount - 1
        if (backStackEntryCount > -1) {
            val entry = fm.getBackStackEntryAt(backStackEntryCount)
            val fragment = fm.findFragmentByTag(entry.name)
            if (fragment is CanChangeStatusBarColor) {
                return fragment
            }
        }
        return null
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
        val context = panel.context

        when (newState) {
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                // TODO check if it needed
                val playerTheme = context.playerTheme()
                if (playerTheme.isFullscreen() || playerTheme.isBigImage()) {
                    activity.window.removeLightStatusBar()
                } else {
                    activity.window.setLightStatusBar()
                }
            }
            SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                searchForDetailFragment()?.adjustStatusBarColor()
                    ?: activity.window.setLightStatusBar()
            }
        }
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

}