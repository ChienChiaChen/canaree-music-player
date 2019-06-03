package dev.olog.msc.presentation.home

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.presentation.base.extensions.panelHeight
import dev.olog.msc.presentation.base.interfaces.SuperCerealBottomSheetBehavior
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.utils.clamp
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

/**
 * Adjust toolbar, tab layout(if present), bottom navigation and sliding panel sliding when a list
 * is scrollied
 */
class OnScrollSlidingCerealBehavior @Inject constructor(private val activity: AppCompatActivity) : DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    private val slidingPanel by lazyFast { BottomSheetBehavior.from(activity.slidingPanel) as SuperCerealBottomSheetBehavior }
    private val bottomNavigation by lazyFast { activity.bottomWrapper }
    private val blurView: View? by lazyFast { activity.blurView }
    private var toolbarStack = Stack<View>()
    private var tabBarStack = Stack<View>()
    private var fabMap = SparseArray<View>()

    private val superCerialSlidingPanelListener by lazyFast { SuperCerealBottomSheetCallback() }

    private val slidingPanelHeight by lazyFast {
        activity.dimen(R.dimen.sliding_panel_peek)
    }
    private val slidingPanelPlusNavigationHeight by lazyFast {
        activity.dimen(R.dimen.sliding_panel_peek) + activity.dimen(R.dimen.bottom_navigation_height)
    }

    override fun onResume(owner: LifecycleOwner) {
        slidingPanel.addPanelSlideListener(superCerialSlidingPanelListener)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, true)
    }

    override fun onPause(owner: LifecycleOwner) {
        slidingPanel.removePanelSlideListener(superCerialSlidingPanelListener)
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(callbacks)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            var clampedToolbarTranslation = 0f
            if (toolbarStack.isNotEmpty()){
                val toolbar = toolbarStack.peek()
                if (toolbar != null) {
                    clampedToolbarTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat(), 0f)
                    toolbar.translationY = clampedToolbarTranslation

                }
            }
            if (tabBarStack.isNotEmpty()){
                tabBarStack.peek().translationY = clampedToolbarTranslation
            }

            val clampedNavigationTranslation =
                    clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
            val clampedSlidingPanelTranslationY = clamp(
                    slidingPanelPlusNavigationHeight - clampedNavigationTranslation.toInt(),
                    slidingPanelHeight,
                    slidingPanelPlusNavigationHeight
            )
            slidingPanel.panelHeight = clampedSlidingPanelTranslationY
            blurView?.translationY = clampedNavigationTranslation
            bottomNavigation.translationY = clampedNavigationTranslation

            fabMap.forEach { key, value ->
                // not very taxing because this collection will have few elements inside
                value.translationY = clampedNavigationTranslation
            }
        }
    }

    private inner class SuperCerealBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {

        private var lastState = BottomSheetBehavior.STATE_COLLAPSED
        private var lastCollapsedTranslationY = bottomNavigation.translationY

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val translationY = clamp(
                    bottomNavigation.height * clamp(slideOffset, 0f, 1f),
                    lastCollapsedTranslationY,
                    bottomNavigation.height.toFloat()
            )
            bottomNavigation.translationY = translationY
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (lastState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
                lastCollapsedTranslationY = bottomNavigation.translationY
            }
            lastState = newState
        }
    }


    private val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        private fun <T : View> View.findViewByIdNotRecursive(id: Int): T? {
            if (this is ViewGroup) {
                forEach { child ->
                    if (child.id == id) {
                        return child as T
                    }
                }
            }
            return null
        }

        private fun hasFragmentOwnership(tag: String?): Boolean {
            return tag?.startsWith("dev.olog.msc") == true
        }

        private fun isPlayerTag(tag: String?): Boolean {
            return tag?.contains("Player") == true
        }

        private fun isViewPagerChildTag(tag: String?): Boolean {
            // TODO check after migratin to viewpager 2
            return tag?.startsWith("android:switcher:") == true
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (hasFragmentOwnership(f.tag) && !isPlayerTag(f.tag) && !isViewPagerChildTag(f.tag)) {
                println("search tab and toolbar of ${f.tag}")

                // since it can be only 1 tab layout and toolbar per visible screen,
                // just override the last value when find a new one tab layout or toolbar
                f.view?.findViewByIdNotRecursive<View>(R.id.tabLayout)?.apply {
                    tabBarStack.push(this)
                }
                f.view?.findViewByIdNotRecursive<View>(R.id.toolbar)?.apply {
                    toolbarStack.push(this)
                }


            }
            if (!isPlayerTag(f.tag)) {
                var recyclerView = f.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
                if (recyclerView == null && f.tag == Fragments.SETTINGS) {
                    // search for settings list
                    recyclerView = f.view?.findViewById(R.id.recycler_view)
                }
                recyclerView?.addOnScrollListener(onScrollListener)?.also {
                    val fab = f.view?.findViewById<View>(R.id.fab)
                    if (fab != null && fab.isVisible) {
                        // add only visible fabs
                        fabMap.append(f.tag!!.hashCode(), fab)
                    }
                    println("adding scroll listener to ${f.tag}")
                }
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            if (hasFragmentOwnership(f.tag) && !isPlayerTag(f.tag) && !isViewPagerChildTag(f.tag)){
                f.view?.findViewByIdNotRecursive<View>(R.id.tabLayout)?.apply {
                    tabBarStack.pop()
                }
                f.view?.findViewByIdNotRecursive<View>(R.id.toolbar)?.apply {
                    toolbarStack.pop()
                }
            }
            println("on fragment paused ${f.tag}")
            f.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)?.removeOnScrollListener(onScrollListener)?.also {
                fabMap.remove(f.tag!!.hashCode())
            }
        }
    }

}