package dev.olog.msc.presentation.home

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.presentation.base.extensions.isExpanded
import dev.olog.msc.presentation.base.extensions.removeLightStatusBar
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.presentation.base.interfaces.CanChangeStatusBarColor
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import dev.olog.msc.shared.ui.theme.playerTheme
import dev.olog.msc.shared.utils.isMarshmallow
import javax.inject.Inject

/**
 * Handle status bar color on SDK >= 23 when:
 * 1) Sliding panel changes state
 * 2) Entering a CanChangeStatusBarColor fragment (probably only DetailFragment)
 */
class StatusBarColorBehavior @Inject constructor(
    private val activity: AppCompatActivity

) : DefaultLifecycleObserver, FragmentManager.OnBackStackChangedListener {

    private val slidingPanel by lazy { (activity as HasSlidingPanel?)?.getSlidingPanel() }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!isMarshmallow()) {
            return
        }

        slidingPanel?.addPanelSlideListener(slidingPanelListener)
        activity.supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        if (!isMarshmallow()) {
            return
        }

        slidingPanel?.removePanelSlideListener(slidingPanelListener)
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
            if (slidingPanel?.isExpanded() == true) {
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

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            val context = bottomSheet.context

            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // TODO check if it needed
                    val playerTheme = context.playerTheme()
                    if (playerTheme.isFullscreen() || playerTheme.isBigImage()) {
                        activity.window.removeLightStatusBar()
                    } else {
                        activity.window.setLightStatusBar()
                    }
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    searchForDetailFragment()?.adjustStatusBarColor() ?: activity.window.setLightStatusBar()
                }
            }
        }
    }

}